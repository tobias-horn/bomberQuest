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
        com.badlogic.gdx.physics.box2d.Box2D.init(); // Initialize Box2D once
    }

    private static final float TIME_STEP = 1f / 120f;
    private static final int VELOCITY_ITERATIONS = 6;
    private static final int POSITION_ITERATIONS = 2;

    private float physicsTime = 0;

    private final BomberQuestGame game;
    private final World world;
    private final Hud hud;

    private int concurrentBombCount = 1;
    private int blastRadius = 1;

    // Player, enemies, bombs, map of all objects:
    private Player player;
    private List<Enemy> enemies = new ArrayList<>();
    private final List<Bomb> bombs = new ArrayList<>();
    private final Map<Vector2, GameObject> map = new HashMap<>();

    // Dimensions in tiles
    private int width = 0;
    private int height = 0;
    private Sound bombPlacedSound = Gdx.audio.newSound(Gdx.files.internal("assets/audio/bombPlaced.mp3"));




    public GameMap(BomberQuestGame game, FileHandle fileHandle, Hud hud) {
        this.game = game;
        this.hud = hud;
        this.world = new World(Vector2.Zero, true);

        MapParser.parseMap(this, fileHandle);
        markBorderWalls();




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


                    switch (powerUp.getType()) {
                        case BLASTRADIUS -> {
                            blastRadius = (blastRadius <= 8) ? blastRadius++ : 8;
                            Gdx.app.log("PowerUp", "Blast radius is now " + blastRadius);
                        }
                        case CONCURRENTBOMB -> {
                            concurrentBombCount = (concurrentBombCount <= 8) ? concurrentBombCount++ : 8;  // Allow one more bomb at once
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
     * Called each frame to remove power-ups that have been used or have expired.
     */
    private void removeMarkedPowerUps() {
        List<Vector2> toRemovePositions = new ArrayList<>();

        for (Map.Entry<Vector2, GameObject> entry : map.entrySet()) {
            if (entry.getValue() instanceof PowerUp p && p.isMarkedForRemoval()) {
                toRemovePositions.add(entry.getKey());
            }
        }
        for (Vector2 pos : toRemovePositions) {
            removeObjectAt((int) pos.x, (int) pos.y);
        }
    }

    /**
     * Decrements the timeRemaining on active power-ups and removes expired ones.
     * Call this as part of your per-frame updates (in tick).
     */

    /**
     * Update the game logic (called every frame).
     */
    public void tick(float frameTime) {

        enemies.removeIf(enemy -> enemy.getBody() == null);

        for (Enemy enemy : enemies) {
            enemy.tick(frameTime);
        }


        bombs.removeIf(b -> {
            b.update(frameTime);
            return b.isHasExploded();
        });





        doPhysicsStep(frameTime);


        removeMarkedPowerUps();

        boolean allEnemiesDead = enemies.isEmpty();
        for (GameObject obj : map.values()) {
            if (obj instanceof Exit exitObj) {
                exitObj.setActive(allEnemiesDead);
            }
        }


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
     * Physics steps
     */
    private void doPhysicsStep(float frameTime) {
        this.physicsTime += frameTime;

        while (this.physicsTime >= TIME_STEP) {
            world.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            this.physicsTime -= TIME_STEP;
        }
    }

    /**
     * Create an object in the game world.
     */
    public void createObject(int x, int y, int objectType) {
        if (x + 1 > width)  width = x + 1;
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
        }
    }

    /**
     * Mark border walls as border.
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
     * Adds a bomb to the game
     */
    public void addBomb(Bomb bomb) {

        int bombsActive = 0;
        for (Bomb b : bombs) {
            if (!b.isHasExploded()) {
                bombsActive++;
            }
        }

        if (bombsActive >= concurrentBombCount) {
            return;
        }
        bombs.add(bomb);
        bombPlacedSound.play();
    }


    private boolean isOnBorder(int x, int y) {
        return x == 0 || y == 0 || x == width - 1 || y == height - 1;
    }

    /**
     * Removes the object from (x,y) in the map and destroys its physics body.
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
     */
    public GameObject getObjectAt(int x, int y) {
        return map.get(new Vector2(x, y));
    }

    /**
     * Whether or not this tile is walkable (no walls).
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



    public Collection<GameObject> getAllObjects() {
        return map.values();
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public BomberQuestGame getGame() {
        return game;
    }

    public World getWorld() {
        return world;
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

    public void setEnemies(List<Enemy> enemies) {
        this.enemies = enemies;
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
