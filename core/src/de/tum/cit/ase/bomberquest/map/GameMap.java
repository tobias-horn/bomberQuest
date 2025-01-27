package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import de.tum.cit.ase.bomberquest.BomberQuestGame;
import de.tum.cit.ase.bomberquest.bonusFeatures.SpeedPowerUp;
import de.tum.cit.ase.bomberquest.objects.*;
import de.tum.cit.ase.bomberquest.screens.Hud;
import de.tum.cit.ase.bomberquest.bonusFeatures.Score;

import java.util.*;

/**
 * Represents the game map, which holds all the objects and entities in the game.
 * Manages map dimensions, game objects, physics updates, and interactions.
 */
public class GameMap {

    // static code runs once when class is loaded into memory
    static {
        com.badlogic.gdx.physics.box2d.Box2D.init(); // Initialize Box2D for physics simulation
    }

    private static final float TIME_STEP = 1f / 120f; // Fixed time step for physics updates
    private static final int VELOCITY_ITERATIONS = 6; // Physics velocity iterations per step
    private static final int POSITION_ITERATIONS = 2; // Physics position iterations per step

    private float physicsTime = 0; // Time accumulator for physics updates

    // References to core game components
    private final BomberQuestGame game;
    private final World world;
    private final Hud hud;

    private int concurrentBombCount = 1; // Number of bombs player can place at once
    private int blastRadius = 1; // Radius of bomb explosions
    private int remainingEnemiesCount = 0;

    // Score management
    private Score score;

    // Player and game objects
    private Player player;
    private List<Enemy> enemies = new ArrayList<>(); // List of enemies in the game
    private final List<Bomb> bombs = new ArrayList<>(); // List of active bombs
    private final Map<Vector2, GameObject> map = new HashMap<>(); // Map of game objects and position
    private final List<ExplosionTile> explosionTiles = new ArrayList<>(); // Active explosion tiles

    // Map dimentions
    private int width = 0;
    private int height = 0;
    private Sound bombPlacedSound = Gdx.audio.newSound(Gdx.files.internal("assets/audio/bombPlaced.mp3")); // Audio FX for bomb placement

    /**
     * Constructor for the GameMap class.
     * @param game The main game instance.
     * @param fileHandle The file containing the map definition.
     * @param hud The game's HUD for displaying stats.
     */
    public GameMap(BomberQuestGame game, FileHandle fileHandle, Hud hud, Score score) {
        this.game = game;
        this.hud = hud;
        this.world = new World(Vector2.Zero, true); // New physics world with no gravity
        this.score = score;
        hud.setSpeedPowerUpActive(false);

        // Parse the map and initialize objects
        MapParser.parseMap(this, fileHandle);
        markBorderWalls();

        // Collision detection
        // Adapted from https://www.gamedevelopment.blog/full-libgdx-game-tutorial-box2d-contact-listener/
        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                Fixture fixtureA = contact.getFixtureA();
                Fixture fixtureB = contact.getFixtureB();
                Object objData1 = fixtureA.getBody().getUserData();
                Object objData2 = fixtureB.getBody().getUserData();

                // If Player collides with Enemy -> game over
                boolean isPlayerEnemyCollision =
                        (objData1 instanceof Player && objData2 instanceof Enemy) ||
                                (objData2 instanceof Player && objData1 instanceof Enemy);

                if (isPlayerEnemyCollision) {
                    Gdx.app.log("Collision", "Player collided with an Enemy!");
                    game.goToGameOver();
                }

                // If Player collides with a PowerUp -> pick it up
                boolean isPlayerPowerUpCollision =
                        (objData1 instanceof Player && objData2 instanceof PowerUp) ||
                                (objData2 instanceof Player && objData1 instanceof PowerUp);

                if (isPlayerPowerUpCollision) {
                    Gdx.app.log("Collision", "Player collided with a PowerUp!");
                    PowerUp powerUp = (objData1 instanceof PowerUp)
                            ? (PowerUp) objData1
                            : (PowerUp) objData2;
                    PowerUp.playSound();
                    powerUp.markForRemoval();

                    // Award points for picking up power-up
                    score.addPointsForPowerUp();

                    // power-up application
                    switch (powerUp.getType()) {
                        case BLASTRADIUS -> {
                            blastRadius = Math.min(blastRadius + 1, 8); // Increase blast radius, max 8
                            Gdx.app.log("PowerUp", "Blast radius is now " + blastRadius);
                        }
                        case CONCURRENTBOMB -> {
                            concurrentBombCount = Math.min(concurrentBombCount + 1, 8); // Increase bomb count, max 8
                            Gdx.app.log("PowerUp", "Concurrent bombs is now " + concurrentBombCount);
                        }
                        case SPEED -> {
                            // The logic for speed here,
                            // though you might prefer your separate SpeedPowerUp collision logic.
                            player.activateSpeedPowerUp(30f);
                            hud.setSpeedPowerUpActive(true);
                        }
                        default ->
                                Gdx.app.error("GameMap", "Unknown PowerUpType: " + powerUp.getType());
                    }
                }


                boolean isPlayerSpeedCollision =
                        (objData1 instanceof Player && objData2 instanceof SpeedPowerUp) ||
                                (objData2 instanceof Player && objData1 instanceof SpeedPowerUp);

                if (isPlayerSpeedCollision) {
                    Gdx.app.log("Collision", "Player collided with a Speed PowerUp!");
                    SpeedPowerUp powerUp = (objData1 instanceof SpeedPowerUp)
                            ? (SpeedPowerUp) objData1
                            : (SpeedPowerUp) objData2;

                    // Play pick-up sound
                    SpeedPowerUp.playSound();
                    powerUp.markForRemoval();

                    // Add points for picking up a power-up if you wish
                    score.addPointsForPowerUp();

                    // Activate the speed effect: double speed for 30s
                    player.activateSpeedPowerUp(30f);

                    // Update HUD to show speed icon
                    hud.setSpeedPowerUpActive(true);
                }



            }
            @Override public void endContact(Contact contact) {}
            @Override public void preSolve(Contact contact, Manifold oldManifold) {}
            @Override public void postSolve(Contact contact, ContactImpulse impulse) {}
        });
    }

    /**
     * Removes power-ups that have been marked for removal from the map.
     */
    private void removeMarkedPowerUps() {
        List<Vector2> toRemovePositions = new ArrayList<>();

        // Find all power-ups marked for removal
        for (Map.Entry<Vector2, GameObject> entry : map.entrySet()) {
            if (entry.getValue() instanceof PowerUp p && p.isMarkedForRemoval()) {
                toRemovePositions.add(entry.getKey());
            }
        }
        // Remove marked power-ups from the map
        for (Vector2 pos : toRemovePositions) {
            removeObjectAt((int) pos.x, (int) pos.y);
        }
    }

    /**
     * Updates the game logic. This method is called once per frame.
     * @param frameTime Time elapsed since the last frame.
     */
    public void tick(float frameTime) {

        enemies.removeIf(enemy -> enemy.getBody() == null); // Remove enemies without physics bodies
        // Update remaining enemies count
        remainingEnemiesCount = enemies.size();
        hud.setRemainingEnemiesCount(remainingEnemiesCount);
        for (Enemy enemy : enemies) {
            enemy.tick(frameTime); // Update each enemy
        }

        // Update bombs and remove exploded ones
        bombs.removeIf(b -> {
            b.update(frameTime);
            return b.isExplosionFinished(); // remove from list only if bomb’s explosionTimer is done
        });

        // Update explosion tiles and remove finished ones
        for (ExplosionTile tile : explosionTiles) {
            tile.update(frameTime);
        }
        explosionTiles.removeIf(ExplosionTile::isFinished);

        // Collect walls to be removed after the loop
        // This is needed because we ran into a ConcurrentModificationException Error when trying to remove it from the collection while iterating over it
        // Adapted from https://stackoverflow.com/questions/5113016/getting-a-concurrentmodificationexception-thrown-when-removing-an-element-from-a

        // Step the physics simulation
        doPhysicsStep(frameTime);
        // Remove marked power-ups
        removeMarkedPowerUps();

        // Activate exit if all enemies are dead
        boolean allEnemiesDead = enemies.isEmpty();
        for (GameObject obj : map.values()) {
            if (obj instanceof Exit exitObj) {
                exitObj.setActive(allEnemiesDead);
            }
        }

        // Check if player is on an active exit tile
        if (player != null) {
            int px = (int) Math.floor(player.getX());
            int py = (int) Math.floor(player.getY());
            GameObject below = getObjectAt(px, py);
            if (below instanceof Exit exit && exit.isActive()) {

                score.addTimeBonus(hud.getTimerInSeconds());
                game.goToGameWon();
            }
        }

        if (player != null && player.getSpeedTimer() <= 0f) {
            hud.setSpeedPowerUpActive(false);
        }
    }

    /**
     * Advances the physics simulation in fixed time steps.
     * @param frameTime Time elapsed since the last frame.
     * Adapted from https://www.reddit.com/r/libgdx/comments/5ib2q3/trying_to_get_my_head_around_fixed_timestep_in/
     */
    private void doPhysicsStep(float frameTime) {
        this.physicsTime += frameTime;

        while (this.physicsTime >= TIME_STEP) {
            world.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            this.physicsTime -= TIME_STEP;
        }
    }

    /**
     * Creates a game object at the specified location.
     * @param x X-coordinate (in tiles).
     * @param y Y-coordinate (in tiles).
     * @param objectType Type of object to create.
     */
    public void createObject(int x, int y, int objectType) {
        if (x + 1 > width)  width = x + 1;
        if (y + 1 > height) height = y + 1;

        // Create an object based on its type
        switch (objectType) {
            case 0 -> map.put(new Vector2(x, y), new IndestructibleWall(world, x, y));
            case 1 -> map.put(new Vector2(x, y), new DestructibleWall(world, x, y, false, null));
            case 2 -> {
                map.put(new Vector2(x, y), new Entrance(world, x, y));
                if (player == null) player = new Player(world, x, y);
            }
            case 3 -> {
                Enemy enemy = new Enemy(world, x, y, this);
                map.put(new Vector2(x, y), enemy);
                enemies.add(enemy);
            }
            case 4 -> map.put(new Vector2(x, y), new DestructibleWall(world, x, y, true, null));
            case 5 -> map.put(new Vector2(x, y), new DestructibleWall(world, x, y, false, PowerUpType.CONCURRENTBOMB));
            case 6 -> map.put(new Vector2(x, y), new DestructibleWall(world, x, y, false, PowerUpType.BLASTRADIUS));
            case 7 -> map.put(new Vector2(x, y), new DestructibleWall(world, x, y, false, PowerUpType.SPEED));
        }
    }

    /**
     * Marks walls on the border of the map as border walls.
     */
    private void markBorderWalls() {
        for (Map.Entry<Vector2, GameObject> entry : map.entrySet()) {
            if (entry.getValue() instanceof IndestructibleWall wall) {
                int x = (int) entry.getKey().x;
                int y = (int) entry.getKey().y;
                if (isOnBorder(x, y)) {
                    wall.setBorderWall(true);
                }
            }
        }
    }

    /**
     * Adds a bomb to the game, if the player has not exceeded their bomb limit.
     * @param bomb The bomb to add.
     */
    public void addBomb(Bomb bomb) {
        // Count active bombs
        int bombsActive = 0;

        for (Bomb b : bombs) {
            if (!b.isHasExploded()) {
                bombsActive++;
            }
        }

        if (bombsActive >= concurrentBombCount) {
            return; // Do not add more bombs if limit is reached
        }
        bombs.add(bomb);
        bombPlacedSound.play();
    }

    /**
     * Checks if a tile is on the border of the map.
     * @param x X-coordinate (in tiles).
     * @param y Y-coordinate (in tiles).
     * @return True if the tile is on the border, false otherwise.
     */
    private boolean isOnBorder(int x, int y) {
        return x == 0 || y == 0 || x == width - 1 || y == height - 1;
    }

    /**
     * Removes the object from (x,y) in the map and destroys its physics body.
     * @param x X-coordinate (in tiles).
     * @param y Y-coordinate (in tiles).
     */
    public void removeObjectAt(int x, int y) {
        System.out.println("removeObjectAt called for tile (" + x + "," + y + ")");
        GameObject removedObj = map.remove(new Vector2(x, y));

        if (removedObj != null) {
            System.out.println("Removed object: " + removedObj);

            if (removedObj.getBody() != null) {
                removedObj.getBody().getWorld().destroyBody(removedObj.getBody());
                removedObj.setBody(null);
            }
        }


        if (removedObj instanceof DestructibleWall dw) {

            if (dw.isExitUnderneath()) {
                boolean activeState = enemies.isEmpty();
                Exit exit = new Exit(world, x, y, activeState);
                Gdx.app.log("GameMap", "Placing Exit at (" + x + ", " + y + ")");
                map.put(new Vector2(x, y), exit);
            }

            else if (dw.getPowerUpUnderneath() != null) {
                PowerUpType type = dw.getPowerUpUnderneath();
                switch (type) {
                    case SPEED -> {

                        SpeedPowerUp speedPowerUp = new SpeedPowerUp(world, x, y);
                        map.put(new Vector2(x, y), speedPowerUp);
                    }
                    case BLASTRADIUS, CONCURRENTBOMB -> {

                        PowerUp powerUp = new PowerUp(world, x, y, type);
                        map.put(new Vector2(x, y), powerUp);
                    }
                }
            }
        }
    }

    /**
     * Returns the Map object at (x,y), or null if none.
     * @param x X-coordinate (in tiles).
     * @param y Y-coordinate (in tiles).
     * @return The GameObject at the specified location, or null if none.
     */
    public GameObject getObjectAt(int x, int y) {
        return map.get(new Vector2(x, y));
    }

    /**
     * Checks whether a tile is walkable.
     * @param x X-coordinate (in tiles).
     * @param y Y-coordinate (in tiles).
     * @return True if the tile is walkable, false otherwise.
     */
    public boolean isTileWalkable(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return false;
        }
        GameObject obj = getObjectAt(x, y);
        if (obj == null) {
            return true;
        }
        // Consider walls or destructible walls as not walkable
        return !(obj instanceof IndestructibleWall || obj instanceof DestructibleWall);
    }

    /**
     * Adds an explosion tile to the game.
     * @param tile The ExplosionTile to add.
     */
    public void addExplosionTile(ExplosionTile tile) {
        explosionTiles.add(tile);
    }

    //Getters and Setters

    public Collection<GameObject> getAllObjects() {
        return map.values();
    }

    public Player getPlayer() {
        return player;
    }

    public BomberQuestGame getGame() {
        return game;
    }

    public World getWorld() {
        return world;
    }

    public List<Bomb> getBombs() {
        return bombs;
    }

    public List<ExplosionTile> getExplosionTiles() {
        return explosionTiles;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public Map<Vector2, GameObject> getMap() {
        return map;
    }

    public int getConcurrentBombCount() {
        return concurrentBombCount;
    }

    public int getBlastRadius() {
        return blastRadius;
    }

    public int getRemainingEnemiesCount() {
        return remainingEnemiesCount;
    }

    /**
     * Returns the Score object for this map.
     * @return The Score instance.
     */
    public Score getScore() {
        return score;
    }
}
