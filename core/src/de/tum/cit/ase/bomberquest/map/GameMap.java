package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import de.tum.cit.ase.bomberquest.BomberQuestGame;
import de.tum.cit.ase.bomberquest.map.MapParser;

import java.util.*;

/**
 * Represents the game map.
 * Holds all the objects and entities in the game.
 */
public class GameMap {

    // A static block is executed once when the class is referenced for the first time.
    static {
        // Initialize the Box2D physics engine.
        com.badlogic.gdx.physics.box2d.Box2D.init();
    }

    // Box2D physics simulation parameters
    private static final float TIME_STEP = 1f / 120f;
    private static final int VELOCITY_ITERATIONS = 6;
    private static final int POSITION_ITERATIONS = 2;
    private float physicsTime = 0;
    private List<Enemy> enemies = new ArrayList<>();


    private final BomberQuestGame game;
    private final World world;
    private Player player;

    private int width = 0;
    private int height = 0;

    private final Map<Vector2, GameObject> map = new HashMap<>();

    public GameMap(BomberQuestGame game, FileHandle fileHandle) {
        this.game = game;
        this.world = new World(Vector2.Zero, true);
        MapParser.parseMap(this, fileHandle);
        markBorderWalls();
    }

    public void spawnEnemies(int count) {
        Random random = new Random();

        // Get the player's position
        Vector2 playerPosition = new Vector2(player.getX(), player.getY());
        float minDistance = 5.0f; // Minimum distance from the player in tiles

        int attempts = 0; // Limit retries to avoid infinite loops
        int maxAttempts = 100; // Maximum number of retries for each enemy

        for (int i = 0; i < count; i++) {
            boolean validPositionFound = false;

            while (!validPositionFound && attempts < maxAttempts) {
                // Generate random x, y positions within the map bounds
                int x = random.nextInt(width); // Ensure within map width
                int y = random.nextInt(height); // Ensure within map height
                Vector2 position = new Vector2(x, y);

                // Check if the position is valid
                if (!map.containsKey(position) && position.dst(playerPosition) >= minDistance) {
                    // Spawn enemy if position is valid
                    Enemy enemy = new Enemy(world, x, y);
                    map.put(position, enemy); // Add enemy to the map
                    enemies.add(enemy); // Add to the enemies list
                    validPositionFound = true; // Exit the retry loop for this enemy
                }

                attempts++;
            }

            // If we couldn't find a valid position, log a warning
            if (!validPositionFound) {
                Gdx.app.log("SpawnEnemies", "Could not find a valid position for enemy " + i);
            }
        }
    }




    public void createObject(int x, int y, int objectType) {
        // Track maximum width/height
        if (x + 1 > width) width = x + 1;
        if (y + 1 > height) height = y + 1;

        switch (objectType) {
            case 0 -> {
                map.put(new Vector2(x, y), new IndestructibleWall(world, x, y));
            }
            case 1 -> {
                map.put(new Vector2(x, y), new DestructibleWall(world, x, y, false, null));
            }
            case 2 -> {
                map.put(new Vector2(x, y), new Entrance(world, x, y));
                if (player == null) {
                    player = new Player(world, x, y);
                }
            }
            case 3 -> {
                Enemy enemy = new Enemy(world, x, y);
                map.put(new Vector2(x, y), enemy); // Add the enemy to the map
                enemies.add(enemy); // Add to an enemies list for further game logic
            }
            case 4 -> {
                map.put(new Vector2(x, y), new DestructibleWall(world, x, y, true, null));
            }
            case 5 -> {
                map.put(new Vector2(x, y), new DestructibleWall(world, x, y, false, PowerUpType.CONCURRENTBOMB));
            }
            case 6 -> {
                map.put(new Vector2(x, y), new DestructibleWall(world, x, y, false, PowerUpType.BLASTRADIUS));
            }
        }
    }

    private void markBorderWalls() {
        for (Map.Entry<Vector2, GameObject> entry : map.entrySet()) {
            GameObject obj = entry.getValue();
            if (obj instanceof IndestructibleWall wall) {
                int x = (int) entry.getKey().x;
                int y = (int) entry.getKey().y;
                if (isOnBorder(x, y)) {
                    wall.setBorderWall(true);
                }
            }
        }
    }

    private boolean isOnBorder(int x, int y) {
        return x == 0 || y == 0 || x == width - 1 || y == height - 1;
    }

    /**
     * Updates the game state. This is called once per frame.
     * @param frameTime the time that has passed since the last update
     */
    public void tick(float frameTime) {
        for (Enemy enemy : enemies) {
            enemy.tick(frameTime);
        }
        doPhysicsStep(frameTime);
    }

    /**
     * Performs as many physics steps as necessary to catch up to the given frame time.
     * @param frameTime Time since last frame in seconds
     */
    private void doPhysicsStep(float frameTime) {
        this.physicsTime += frameTime;

        while (this.physicsTime >= TIME_STEP) {
            this.world.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            this.physicsTime -= TIME_STEP;
        }
    }

    /**
     * Renders all objects on the map.
     * Here we subtract 0.5 tile from the center so the sprite aligns with the collision box.
     *
     */

    public Collection<GameObject> getAllObjects() {
        return map.values();
    }

    /** Returns the player on the map. */
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

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public void setEnemies(List<Enemy> enemies) {
        this.enemies = enemies;
    }
}
