package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import de.tum.cit.ase.bomberquest.BomberQuestGame;
import de.tum.cit.ase.bomberquest.objects.*;
import de.tum.cit.ase.bomberquest.screens.Hud;
import de.tum.cit.ase.bomberquest.textures.Textures; // for the HUD icons
import java.util.*;

/**
 * Represents the game map, which holds all the objects and entities in the game.
 * Manages map dimensions, game objects, physics updates, and interactions.
 */
public class GameMap {

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
    public GameMap(BomberQuestGame game, FileHandle fileHandle, Hud hud) {
        this.game = game;
        this.hud = hud;
        this.world = new World(Vector2.Zero, true); // New physics world with no gravity

        // Parse the map and initialize objects
        MapParser.parseMap(this, fileHandle);
        markBorderWalls();

        // Collision detection
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
                        default ->
                                Gdx.app.error("GameMap", "Unknown PowerUpType: " + powerUp.getType());
                    }
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
        List<Vector2> wallsToRemove = new ArrayList<>();

        // Remove destructible walls with finished animations
        for (Map.Entry<Vector2, GameObject> entry : map.entrySet()) {
            GameObject obj = entry.getValue();
            if (obj instanceof DestructibleWall wall) {
                wall.update(frameTime); // Update animation time
                if (wall.isFadedAway()) {
                    wallsToRemove.add(entry.getKey());
                }
            }
        }
        for (Vector2 pos : wallsToRemove) {
            removeObjectAt((int) pos.x, (int) pos.y);
        }

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
                game.goToGameWon();
            }
        }
    }

    /**
     * Advances the physics simulation in fixed time steps.
     * @param frameTime Time elapsed since the last frame.
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


            if (removedObj instanceof Enemy) {
                enemies.remove(removedObj);
            }

            if (removedObj.getBody() != null) {
                removedObj.getBody().getWorld().destroyBody(removedObj.getBody());
                removedObj.setBody(null);
            }
        }

        if (removedObj instanceof DestructibleWall dw && dw.isExitUnderneath()) {
            boolean activeState = enemies.isEmpty();
            Exit exit = new Exit(world, x, y, activeState);
            map.put(new Vector2(x, y), exit);
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
}
