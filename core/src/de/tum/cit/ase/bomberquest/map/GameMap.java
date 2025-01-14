package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import de.tum.cit.ase.bomberquest.BomberQuestGame;
import de.tum.cit.ase.bomberquest.objects.*;

import java.util.*;

/**
 * Represents the game map, which holds all the objects and entities in the game.
 * Manages map dimensions, game objects, physics updates, and interactions.
 */
public class GameMap {

    // This block runs once when the class is loaded for the first time.
    static {
        // Initializes the Box2D physics engine for handling collisions and movement.
        com.badlogic.gdx.physics.box2d.Box2D.init();
    }

    // Constants for physics simulation accuracy.
    private static final float TIME_STEP = 1f / 120f; // Time between physics updates.
    private static final int VELOCITY_ITERATIONS = 6; // Controls velocity precision.
    private static final int POSITION_ITERATIONS = 2; // Controls position precision.

    // Time used to keep track of physics updates.
    private float physicsTime = 0;

    // Stores all enemies on the map.
    private List<Enemy> enemies = new ArrayList<>();

    // List of all bombs currently on the map.
    private final List<Bomb> bombs = new ArrayList<>();

    // References to the other classes.
    private final BomberQuestGame game;
    private final World world;
    private Player player;

    // Dimensions of the map in tiles.
    private int width = 0;
    private int height = 0;

    // this map is intended to map each position (Vector) to the game object to which it belongs.
    private final Map<Vector2, GameObject> map = new HashMap<>();

    /**
     * GameMap constructor
     * It loads map data, creates the physics world, and sets up collision handling.
     *
     * @param game       The main game instance.
     * @param fileHandle File containing the map layout data.
     */
    public GameMap(BomberQuestGame game, FileHandle fileHandle) {
        this.game = game;
        this.world = new World(Vector2.Zero, true); // Physics world with no gravity.

        // Load the map using a parser
        MapParser.parseMap(this, fileHandle);

        // Mark walls along the map borders.
        markBorderWalls();

        // Collision detection.
        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                // Get the two objects that collided.
                Fixture fixtureA = contact.getFixtureA();
                Fixture FixtureB = contact.getFixtureB();

                Object objData1 = fixtureA.getBody().getUserData();
                Object objData2 = FixtureB.getBody().getUserData();

                // If a Player collides with an Enemy, end the game.
                boolean isPlayerEnemyCollision =
                        (objData1 instanceof Player && objData2 instanceof Enemy) ||
                                (objData2 instanceof Player && objData1 instanceof Enemy);

                if (isPlayerEnemyCollision) {
                    Gdx.app.log("Collision", "Player collided with an Enemy!");
                    game.goToGameOver(); // Trigger game-over state.
                }
            }

            // Unused collision methods that need to be overridden (from ContactListener).
            @Override public void endContact(Contact contact) {}
            @Override public void preSolve(Contact contact, Manifold oldManifold) {}
            @Override public void postSolve(Contact contact, ContactImpulse impulse) {}
        });
    }

    /**
     * Spawns a specific number of enemies.
     * Ensures they are far enough from the player.
     *
     * @param count The number of enemies to spawn.
     */
    public void spawnEnemies(int count) {
        Random random = new Random();
        Vector2 playerPosition = new Vector2(player.getX(), player.getY());
        float minDistance = 5.0f; // Enemies must spawn at least 5 tiles away from player.

        int attempts = 0; // Keeps track of attempts used to place enemies on map.
        int maxAttempts = 100; // Upper limit to avoids infinite loops.

        for (int i = 0; i < count; i++) {
            boolean validPositionFound = false;

            while (!validPositionFound && attempts < maxAttempts) {
                // Randomly pick a position on the map.
                int x = random.nextInt(width);
                int y = random.nextInt(height);
                Vector2 position = new Vector2(x, y);

                // Only place enemies on empty tiles far from the player.
                if (!map.containsKey(position) && position.dst(playerPosition) >= minDistance) {
                    enemies.add(new Enemy(world, x, y));
                    validPositionFound = true;
                }
                attempts++;
            }

            // Warning if we couldn't place an enemy.
            if (!validPositionFound) {
                Gdx.app.log("SpawnEnemies", "Failed to place enemy " + i);
            }
        }
    }

    /**
     * Creates a game object at the specified position.
     *
     * @param x          X-coordinate of the object.
     * @param y          Y-coordinate of the object.
     * @param objectType The type of object (e.g., wall, entrance, enemy).
     */
    public void createObject(int x, int y, int objectType) {
        // Update the map's width and height if necessary.
        if (x + 1 > width) width = x + 1;
        if (y + 1 > height) height = y + 1;

        // Add objects based on their type.
        switch (objectType) {
            case 0 -> map.put(new Vector2(x, y), new IndestructibleWall(world, x, y));
            case 1 -> map.put(new Vector2(x, y), new DestructibleWall(world, x, y, false, null));
            case 2 -> {
                map.put(new Vector2(x, y), new Entrance(world, x, y));
                if (player == null) player = new Player(world, x, y);
            }
            case 3 -> {
                Enemy enemy = new Enemy(world, x, y);
                map.put(new Vector2(x, y), enemy);
                enemies.add(enemy);
            }
            case 4 -> map.put(new Vector2(x, y), new DestructibleWall(world, x, y, true, null));
            case 5 -> map.put(new Vector2(x, y), new DestructibleWall(world, x, y, false, PowerUpType.CONCURRENTBOMB));
            case 6 -> map.put(new Vector2(x, y), new DestructibleWall(world, x, y, false, PowerUpType.BLASTRADIUS));
        }
    }

    /**
     * Marks indestructible walls along the borders of the map.
     */
    private void markBorderWalls() {
        for (Map.Entry<Vector2, GameObject> entry : map.entrySet()) {
            if (entry.getValue() instanceof IndestructibleWall wall) {
                int x = (int) entry.getKey().x;
                int y = (int) entry.getKey().y;
                if (isOnBorder(x, y)) wall.setBorderWall(true);
            }
        }
    }

    /**
     * Adds a bomb to the map.
     * @param bomb The bomb to add.
     */
    public void addBomb(Bomb bomb) {
        bombs.add(bomb);
    }

    /**
     * Checks if a tile is on the border of the map.
     * @param x The x-coordinate of the tile.
     * @param y The y-coordinate of the tile.
     * @return True if the tile is on the border, false otherwise.
     */
    private boolean isOnBorder(int x, int y) {
        return x == 0 || y == 0 || x == width - 1 || y == height - 1;
    }

    /**
     * Updates the game state (called once per frame).
     * @param frameTime Time since the last update.
     */
    public void tick(float frameTime) {
        // Update all enemies.
        for (Enemy enemy : enemies) {
            enemy.tick(frameTime);
        }

        // Update all bombs and remove exploded ones.
        bombs.removeIf(b -> {
            b.update(frameTime);
            return b.isHasExploded();
        });

        // Perform necessary physics updates.
        doPhysicsStep(frameTime);
    }

    /**
     * Steps through the physics simulation to keep it in sync with the game.
     * @param frameTime Time since the last frame.
     */
    private void doPhysicsStep(float frameTime) {
        this.physicsTime += frameTime;

        while (this.physicsTime >= TIME_STEP) {
            world.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            this.physicsTime -= TIME_STEP;
        }
    }

    // Standard getter and setter methods for accessing map properties and objects.

    public Collection<GameObject> getAllObjects() {
        return map.values();
    }

    public Player getPlayer() {
        return player;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public float getPhysicsTime() {
        return physicsTime;
    }

    public void setPhysicsTime(float physicsTime) {
        this.physicsTime = physicsTime;
    }

    public List<Bomb> getBombs() {
        return bombs;
    }

    public BomberQuestGame getGame() {
        return game;
    }

    public World getWorld() {
        return world;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Map<Vector2, GameObject> getMap() {
        return map;
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public void setEnemies(List<Enemy> enemies) {
        this.enemies = enemies;
    }

    /**
     * Gets the object from a specific tile.
     * @param x The x-coordinate of the tile.
     * @param y The y-coordinate of the tile.
     * @return The object at the tile, or null if none exists.
     */
    public GameObject getObjectAt(int x, int y) {
        return map.get(new Vector2(x, y));
    }

    /**
     * Removes the object at a specific tile.
     * Destroys its physical body if it exists.
     *
     * @param x The x-coordinate of the tile.
     * @param y The y-coordinate of the tile.
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
    }
}