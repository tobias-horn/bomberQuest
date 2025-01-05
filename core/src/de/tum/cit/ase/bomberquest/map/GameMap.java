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
    
    // Box2D physics simulation parameters (you can experiment with these if you want, but they work well as they are)
    /**
     * The time step for the physics simulation.
     * This is the amount of time that the physics simulation advances by in each frame.
     * It is set to 1/refreshRate, where refreshRate is the refresh rate of the monitor, e.g., 1/60 for 60 Hz.
     */
    private static final float TIME_STEP = 1f / 120f;// Alternative: divide by Gdx.graphics.getDisplayMode().refreshRate;

    /** The number of velocity iterations for the physics simulation. */
    private static final int VELOCITY_ITERATIONS = 6;
    /** The number of position iterations for the physics simulation. */
    private static final int POSITION_ITERATIONS = 2;
    /**
     * The accumulated time since the last physics step.
     * We use this to keep the physics simulation at a constant rate even if the frame rate is variable.
     */
    private float physicsTime = 0;
    
    /** The game, in case the map needs to access it. */
    private final BomberQuestGame game;
    /** The Box2D world for physics simulation. */
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


    public void createObject(int x, int y, int objectType) {
        // Track maximum width/height, etc., if needed
        if(x +1 > width) width = x +1;
        if(y +1 > height) height = y +1;

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
                // Possibly empty tile
                map.put(new Vector2(x, y), null);
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
     * Every dynamic object in the game should update its state here.
     * @param frameTime the time that has passed since the last update
     */
    public void tick(float frameTime) {
        doPhysicsStep(frameTime);
    }
    
    /**
     * Performs as many physics steps as necessary to catch up to the given frame time.
     * This will update the Box2D world by the given time step.
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
     * @param batch the SpriteBatch used for rendering
     */

    public void render(SpriteBatch batch) {
        float tileSizeInWorldUnits = 32; // Match your game grid size (adjust as needed)

        for (GameObject obj : getAllObjects()) {
            if (obj != null) {
                // Debug logs for troubleshooting
                System.out.println("Rendering object: " + obj +
                        " at (" + obj.getX() + ", " + obj.getY() +
                        ") with texture: " + obj.getCurrentAppearance());

                batch.draw(
                        obj.getCurrentAppearance(),
                        obj.getX() * tileSizeInWorldUnits,
                        obj.getY() * tileSizeInWorldUnits,
                        tileSizeInWorldUnits,
                        tileSizeInWorldUnits
                );
                System.out.println("Object rendered at: (" + obj.getX() + ", " + obj.getY() + ")");

            }
        }
    }



    public Collection<GameObject> getAllObjects() {
        return map.values();
    }
    
    /** Returns the player on the map. */
    public Player getPlayer() {
        return player;
    }


}
