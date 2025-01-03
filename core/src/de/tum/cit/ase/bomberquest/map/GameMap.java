package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import de.tum.cit.ase.bomberquest.BomberQuestGame;
import de.tum.cit.ase.bomberquest.map.MapParser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private static final float TIME_STEP = 1f / Gdx.graphics.getDisplayMode().refreshRate;
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

    private final Map<Vector2, GameObject> map = new HashMap<>();

    public GameMap(BomberQuestGame game, FileHandle fileHandle) {
        this.game = game;
        this.world = new World(Vector2.Zero, true);
        MapParser.parseMap(this, fileHandle);
    }


    public void createObject(int x, int y, int objectType){
        switch(objectType){
            case 0 -> map.put(new Vector2(x, y), new IndestructibleWall(world, x, y));
            case 1 -> map.put(new Vector2(x, y), new DestructibleWall(world, x, y, false, null));
            case 2 -> map.put(new Vector2(x, y), new Entrance(world, x, y));
            case 3 -> map.put(new Vector2(x, y), null);
            case 4 -> map.put(new Vector2(x, y), new DestructibleWall(world, x, y, true, null));
            case 5 -> map.put(new Vector2(x, y), new DestructibleWall(world, x, y, false, PowerUpType.CONCURRENTBOMB));
            case 6 -> map.put(new Vector2(x, y), new DestructibleWall(world, x, y, false, PowerUpType.BLASTRADIUS));
        }
    }



    
    /**
     * Updates the game state. This is called once per frame.
     * Every dynamic object in the game should update its state here.
     * @param frameTime the time that has passed since the last update
     */
//    public void tick(float frameTime) {
//        this.player.tick(frameTime);
//        doPhysicsStep(frameTime);
//    }
    
    /**
     * Performs as many physics steps as necessary to catch up to the given frame time.
     * This will update the Box2D world by the given time step.
     * @param frameTime Time since last frame in seconds
     */
//    private void doPhysicsStep(float frameTime) {
//        this.physicsTime += frameTime;
//        while (this.physicsTime >= TIME_STEP) {
//            this.world.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
//            this.physicsTime -= TIME_STEP;
//        }
//    }
    
//    /** Returns the player on the map. */
//    public Player getPlayer() {
//        return player;
//    }
//
//    /** Returns the chest on the map. */
//    public Chest getChest() {
//        return chest;
//    }
//
//    /** Returns the flowers on the map. */
//    public List<Flowers> getFlowers() {
//        return Arrays.stream(flowers).flatMap(Arrays::stream).toList();
//    }
}
