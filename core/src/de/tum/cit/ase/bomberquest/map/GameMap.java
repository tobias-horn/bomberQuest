package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import de.tum.cit.ase.bomberquest.BomberQuestGame;
import de.tum.cit.ase.bomberquest.bonusFeatures.ArrowPowerUp;
import de.tum.cit.ase.bomberquest.bonusFeatures.SpeedPowerUp;
import de.tum.cit.ase.bomberquest.bonusFeatures.Score;
import de.tum.cit.ase.bomberquest.objects.*;
import de.tum.cit.ase.bomberquest.screens.Hud;

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
    private List<Enemy> enemies = new ArrayList<>();        // List of enemies in the game
    private final List<Bomb> bombs = new ArrayList<>();     // List of active bombs
    private final Map<Vector2, GameObject> map = new HashMap<>();
    private final List<ExplosionTile> explosionTiles = new ArrayList<>();
    private final List<Arrow> activeArrows = new ArrayList<>();

    // Map dimensions
    private int width = 0;
    private int height = 0;

    private Sound bombPlacedSound = Gdx.audio.newSound(Gdx.files.internal("assets/audio/bombPlaced.mp3")); // Audio FX for bomb placement

    /**
     * Constructor for the GameMap class.
     *
     * @param game       The main game instance.
     * @param fileHandle The file containing the map definition.
     * @param hud        The game's HUD for displaying stats.
     * @param score      The Score instance for tracking player points.
     */
    public GameMap(BomberQuestGame game, FileHandle fileHandle, Hud hud, Score score) {
        this.game = game;
        this.hud = hud;
        this.world = new World(Vector2.Zero, true);
        this.score = score;
        hud.setSpeedPowerUpActive(false);


        // Parse the map and initialize objects
        MapParser.parseMap(this, fileHandle);
        markBorderWalls();

        // Collision detection
        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                Fixture fixtureA = contact.getFixtureA();
                Fixture fixtureB = contact.getFixtureB();
                Object objDataA = fixtureA.getBody().getUserData();
                Object objDataB = fixtureB.getBody().getUserData();

                // Identify if one of the objects is the player
                Player player = null;
                Object otherObject = null;

                if (objDataA instanceof Player) {
                    player = (Player) objDataA;
                    otherObject = objDataB;
                } else if (objDataB instanceof Player) {
                    player = (Player) objDataB;
                    otherObject = objDataA;
                }

                if (player != null && otherObject != null) {
                    if (otherObject instanceof Enemy) {
                        handlePlayerEnemyCollision();
                    } else if (otherObject instanceof PowerUp) {
                        ((PowerUp) otherObject).markForRemoval();
                    }
                }

                // Arrow-Enemy collision
                if ((objDataA instanceof Arrow && objDataB instanceof Enemy)
                        || (objDataB instanceof Arrow && objDataA instanceof Enemy)) {

                    Arrow arrow;
                    Enemy enemy;
                    if (objDataA instanceof Arrow) {
                        arrow = (Arrow) objDataA;
                        enemy = (Enemy) objDataB;
                    } else {
                        arrow = (Arrow) objDataB;
                        enemy = (Enemy) objDataA;
                    }

                    killEnemyWithArrow(enemy);
                    arrow.markForRemoval();
                }
            }

            @Override
            public void endContact(Contact contact) {}

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {}

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {}

            private void handlePlayerEnemyCollision() {
                Gdx.app.log("Collision", "Player collided with an Enemy!");
                game.goToGameOver();
            }
        });
    }

    /**
     * Removes power-ups that have been marked for removal from the map.
     * Also applies their effects to the player (e.g., speed, arrow).
     */
    private void handlePowerUps() {
        List<Vector2> toRemovePositions = new ArrayList<>();
        List<GameObject> toAwardEffects = new ArrayList<>();

        for (Map.Entry<Vector2, GameObject> entry : map.entrySet()) {
            GameObject obj = entry.getValue();
            if (obj instanceof PowerUp powerUpObj && powerUpObj.isMarkedForRemoval()) {
                toRemovePositions.add(entry.getKey());
                toAwardEffects.add(obj);
            } else if (obj instanceof SpeedPowerUp speedPU && speedPU.isMarkedForRemoval()) {
                toRemovePositions.add(entry.getKey());
                toAwardEffects.add(obj);
            } else if (obj instanceof ArrowPowerUp arrowPU && arrowPU.isMarkedForRemoval()) {
                toRemovePositions.add(entry.getKey());
                toAwardEffects.add(obj);
            }
        }

        // Award the power-up effects
        for (GameObject obj : toAwardEffects) {
            if (obj instanceof PowerUp pu) {
                PowerUp.playSound();
                score.addPointsForPowerUp();
                switch (pu.getType()) {
                    case BLASTRADIUS -> blastRadius = Math.min(blastRadius + 1, 8);
                    case CONCURRENTBOMB -> concurrentBombCount = Math.min(concurrentBombCount + 1, 8);
                    case SPEED -> {
                        player.activateSpeedPowerUp(30f);
                        hud.setSpeedPowerUpActive(true);
                    }
                    case ARROW -> {
                        // Plain arrow in a "PowerUp" destructible wall scenario
                        System.out.println("[handlePowerUps] Plain ARROW power-up. Activating arrow shoot for 30s...");
                        player.activateArrowPowerUp(30f);
                        hud.setArrowPowerUpActive(true);
                    }
                }
            } else if (obj instanceof SpeedPowerUp) {
                // SpeedPowerUp logic
                SpeedPowerUp.playSound();
                score.addPointsForPowerUp();
                player.activateSpeedPowerUp(30f);
                hud.setSpeedPowerUpActive(true);

            } else if (obj instanceof ArrowPowerUp) {
                // ArrowPowerUp logic
                ArrowPowerUp.playSound();
                score.addPointsForPowerUp();
                player.activateArrowPowerUp(30f);
                hud.setArrowPowerUpActive(true);
            }
        }

        // Remove the used power-ups from the map
        for (Vector2 pos : toRemovePositions) {
            removeObjectAt((int) pos.x, (int) pos.y);
        }
    }

    /**
     * Updates the game logic. This method is called once per frame.
     *
     * @param frameTime Time elapsed since the last frame.
     */
    public void tick(float frameTime) {
        // Remove enemies without physics bodies
        enemies.removeIf(enemy -> enemy.getBody() == null);

        // Update remaining enemies count
        remainingEnemiesCount = enemies.size();
        hud.setRemainingEnemiesCount(remainingEnemiesCount);

        // Tick each enemy
        for (Enemy enemy : enemies) {
            enemy.tick(frameTime);
        }

        // Update bombs and remove those with finished explosions
        bombs.removeIf(b -> {
            b.update(frameTime);
            return b.isExplosionFinished();
        });

        // Update explosion tiles
        for (ExplosionTile tile : explosionTiles) {
            tile.update(frameTime);
        }
        // Remove finished explosion tiles
        explosionTiles.removeIf(ExplosionTile::isFinished);

        // Step the physics simulation
        doPhysicsStep(frameTime);

        // Handle power-up removals/collection
        handlePowerUps();

        // Clean up any removed enemies
        enemies.removeIf(e -> {
            if (e.isMarkedForRemoval()) {
                if (e.getBody() != null) {
                    e.getBody().getWorld().destroyBody(e.getBody());
                    e.setBody(null);
                }
                return true;
            }
            return false;
        });

        // Update arrows
        for (Iterator<Arrow> it = activeArrows.iterator(); it.hasNext();) {
            Arrow arrow = it.next();
            arrow.update(frameTime);

            if (arrow.shouldRemove()) {
                arrow.destroyBody();
                it.remove();
            }
        }

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

        // remove speed power up icon when time is up
        if (player != null && player.getSpeedTimer() <= 0f) {
            hud.setSpeedPowerUpActive(false);
        }

        // remove arrow hud icon when time is up
        if (player != null && player.getArrowTimer() <= 0f) {
            hud.setArrowPowerUpActive(false);
        }
    }

    /**
     * Advances the physics simulation in fixed time steps.
     *
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
     *
     * @param x          X-coordinate (in tiles).
     * @param y          Y-coordinate (in tiles).
     * @param objectType Type of object to create.
     */
    public void createObject(int x, int y, int objectType) {
        if (x + 1 > width) width = x + 1;
        if (y + 1 > height) height = y + 1;

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
            case 8 -> map.put(new Vector2(x, y), new DestructibleWall(world, x, y, false, PowerUpType.ARROW));
        }
    }

    /**
     * Marks walls on the border of the map as border walls.
     */
    private void markBorderWalls() {
        int maxX = width - 1;
        int maxY = height - 1;

        for (Map.Entry<Vector2, GameObject> entry : map.entrySet()) {
            if (entry.getValue() instanceof IndestructibleWall wall) {
                int x = (int) entry.getKey().x;
                int y = (int) entry.getKey().y;

                // Check if it's on the border
                if (isOnBorder(x, y, maxX, maxY)) {
                    wall.setBorderWall(true);
                    // Set corner or edge type...
                    if (x == 0 && y == 0) {
                        wall.setBorderWallType(IndestructibleWall.BorderWallType.BOTTOM_LEFT);
                    } else if (x == 0 && y == maxY) {
                        wall.setBorderWallType(IndestructibleWall.BorderWallType.TOP_LEFT);
                    } else if (x == maxX && y == 0) {
                        wall.setBorderWallType(IndestructibleWall.BorderWallType.BOTTOM_RIGHT);
                    } else if (x == maxX && y == maxY) {
                        wall.setBorderWallType(IndestructibleWall.BorderWallType.TOP_RIGHT);
                    } else if (y == 0) {
                        wall.setBorderWallType(IndestructibleWall.BorderWallType.BOTTOM);
                    } else if (y == maxY) {
                        wall.setBorderWallType(IndestructibleWall.BorderWallType.TOP);
                    } else if (x == 0) {
                        wall.setBorderWallType(IndestructibleWall.BorderWallType.LEFT);
                    } else if (x == maxX) {
                        wall.setBorderWallType(IndestructibleWall.BorderWallType.RIGHT);
                    }
                }
            }
        }
    }

    private boolean isOnBorder(int x, int y, int maxX, int maxY) {
        return (x == 0 || y == 0 || x == maxX || y == maxY);
    }

    /**
     * Adds a bomb to the game, if the player has not exceeded their bomb limit.
     *
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
     * Adds an arrow to the game world.
     *
     * @param arrow The arrow to add.
     */
    public void addArrow(Arrow arrow) {
        activeArrows.add(arrow);
    }

    /**
     * Removes the object at (x,y) from the map and destroys its physics body if present.
     *
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
            } else if (dw.getPowerUpUnderneath() != null) {
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
                    case ARROW -> {
                        ArrowPowerUp arrowPowerUp = new ArrowPowerUp(world, x, y);
                        map.put(new Vector2(x, y), arrowPowerUp);
                    }
                }
            }
        }
    }

    /**
     * Returns the Map object at (x,y), or null if none.
     *
     * @param x X-coordinate (in tiles).
     * @param y Y-coordinate (in tiles).
     * @return The GameObject at the specified location, or null if none.
     */
    public GameObject getObjectAt(int x, int y) {
        return map.get(new Vector2(x, y));
    }

    /**
     * Checks whether a tile is walkable.
     *
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
        // Walls or destructible walls are not walkable
        return !(obj instanceof IndestructibleWall || obj instanceof DestructibleWall);
    }

    /**
     * Kills the specified enemy with an arrow, adding score and marking the enemy for removal.
     *
     * @param enemy The enemy to kill.
     */
    public void killEnemyWithArrow(Enemy enemy) {
        System.out.println("Added points for enemy killed by arrow.");
        score.addPointsForEnemyKilled();
        enemy.markForRemoval();
    }

    /**
     * Adds an explosion tile to the game.
     *
     * @param tile The {@link ExplosionTile} to add.
     */
    public void addExplosionTile(ExplosionTile tile) {
        explosionTiles.add(tile);
    }

    // -------------------------
    // Getters and Setters
    // -------------------------

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
     *
     * @return The {@link Score} instance.
     */
    public Score getScore() {
        return score;
    }

    /**
     * Returns the list of currently active arrows on the map.
     *
     * @return A list of {@link Arrow} objects.
     */
    public List<Arrow> getActiveArrows() {
        return activeArrows;
    }
}
