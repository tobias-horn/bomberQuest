package de.tum.cit.ase.bomberquest.objects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.World;
import de.tum.cit.ase.bomberquest.map.AStarPathFinder;
import de.tum.cit.ase.bomberquest.map.GameMap;
import de.tum.cit.ase.bomberquest.textures.Animations;
import de.tum.cit.ase.bomberquest.textures.Drawable;

import java.util.List;

/**
 * Represents an enemy in the game.
 */
public class Enemy extends GameObject implements Drawable {

    /** The elapsed time since the game started, used for animations. */
    private float elapsedTime;

    /** Reference to the GameMap for pathfinding. */
    private GameMap gameMap;

    /** The movement speed of the enemy. */
    private final float speed = 2f;

    // Random wandering
    private float randomWalkTimer = 0f; // Timer for how long to move in one random direction
    private Vector2 randomDirectionVector = new Vector2(0, 0); // Current random direction
    private final float MAX_RANDOM_DIRECTION_TIME = 2.0f; // Max time to move in one random direction

    private enum Direction {
        LEFT, RIGHT, UP, DOWN
    }

    private Direction currentDirection = Direction.DOWN; // Default direction


    /**
     * Constructor for creating an enemy.
     * @param world The Box2D world to add the enemy to.
     * @param x The initial x-coordinate (in tiles).
     * @param y The initial y-coordinate (in tiles).
     */
    /**
     * Constructor for creating an enemy.
     * @param world The Box2D world to add the enemy to.
     * @param x The initial x-coordinate (in tiles).
     * @param y The initial y-coordinate (in tiles).
     * @param gameMap Reference to the game map (for pathfinding).
     */
    public Enemy(World world, float x, float y, GameMap gameMap) {
        super(world, x, y);
        this.gameMap = gameMap;
        this.elapsedTime = 0;
        setDynamicBody();
    }

    /**
     * Changes the body type to DynamicBody, enabling movement and physics interactions.
     */
    private void setDynamicBody() {
        body.setType(BodyDef.BodyType.DynamicBody); // Enemies can move and interact with the environment.
    }

    @Override
    protected void createHitbox(World world, float tileX, float tileY) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody; // Enemies need to be dynamic.
        bodyDef.position.set(tileX + 0.5f, tileY + 0.5f); // Center the hitbox within the tile.

        body = world.createBody(bodyDef);

        CircleShape shape = new CircleShape();
        shape.setRadius(0.3f); // A circular hitbox with a radius of 0.3 tiles.
        body.createFixture(shape, 1.0f);
        shape.dispose();

        body.setUserData(this); // Link the body to this Enemy instance.
    }

    /**
     * Updates the enemy's movement and animation based on elapsed time.
     * @param deltaTime The time elapsed since the last frame.
     */
    public void tick(float deltaTime) {
        elapsedTime += deltaTime;

        // 1) Safety checks
        if (gameMap == null || gameMap.getPlayer() == null) {
            body.setLinearVelocity(0, 0);
            return;
        }

        // 2) Get the enemy's current tile (floored, since A* works on tiles)
        int ex = (int) Math.floor(getX());
        int ey = (int) Math.floor(getY());

        // 3) Get the player's exact position
        float px = gameMap.getPlayer().getX();
        float py = gameMap.getPlayer().getY();

        // 4) Calculate distance to player
        float distToPlayer = new Vector2(getX(), getY()).dst(px, py);

        // 5) Define the chase range in tiles
        float chaseRange = 6f;

        // 6) Only activate A* if the player is within the chase range
        if (distToPlayer <= chaseRange) {
            // Use A* to find a path to the player's current tile
            List<Vector2> path = AStarPathFinder.calculatePath(
                    gameMap,
                    new Vector2(ex, ey),                 // Enemy's current tile
                    new Vector2((int) Math.floor(px), (int) Math.floor(py)) // Player's current tile (fallback for pathfinding)
            );

            if (path.size() > 1) {
                // Follow the next step in the path
                Vector2 nextTile = path.get(1); // The next step in the path
                Vector2 targetPos = new Vector2(nextTile.x + 0.5f, nextTile.y + 0.5f); // Center of the next tile
                Vector2 currentPos = new Vector2(getX(), getY());

                // Move toward the center of the tile
                Vector2 direction = targetPos.sub(currentPos).nor(); // Calculate normalized direction
                body.setLinearVelocity(direction.scl(speed));

                // Update animation direction based on velocity
                updateAnimationDirection(direction.x, direction.y);
                return;
            }
        }

        // 7) Fallback to random wandering if the player is out of range or no path was found
        doRandomWander(deltaTime);
    }

    /**
     * Moves the enemy in a random direction, changing direction every few seconds.
     */
    private void doRandomWander(float deltaTime) {
        randomWalkTimer -= deltaTime;
        if (randomWalkTimer <= 0) {
            // Pick a new random direction
            pickRandomDirection();
            // Reset timer
            randomWalkTimer = MAX_RANDOM_DIRECTION_TIME;
        }

        // Move in that random direction
        float speed = this.speed;
        body.setLinearVelocity(randomDirectionVector.x * speed, randomDirectionVector.y * speed);

        // Update animation direction
        updateAnimationDirection(randomDirectionVector.x, randomDirectionVector.y);
    }

    /** Chooses a random direction among up/down/left/right (or you can do fully any angle). */
    private void pickRandomDirection() {
        // Simple 4-direction approach:
        int dir = (int) (Math.random() * 4);
        switch (dir) {
            case 0: randomDirectionVector.set(1, 0);  break; // right
            case 1: randomDirectionVector.set(-1, 0); break; // left
            case 2: randomDirectionVector.set(0, 1);  break; // up
            default: randomDirectionVector.set(0, -1); // down
        }
    }

    /** Updates the currentDirection enum (used for animations) given a velocity. */
    private void updateAnimationDirection(float vx, float vy) {
        // If x velocity is bigger in magnitude than y velocity
        if (Math.abs(vx) > Math.abs(vy)) {
            if (vx < 0) {
                currentDirection = Direction.LEFT;
            } else {
                currentDirection = Direction.RIGHT;
            }
        } else {
            if (vy > 0) {
                currentDirection = Direction.UP;
            } else {
                currentDirection = Direction.DOWN;
            }
        }
    }

    @Override
    public TextureRegion getCurrentAppearance() {
        switch (currentDirection) {
            case LEFT:
                return Animations.ENEMY_WALK_LEFT.getKeyFrame(elapsedTime, true);
            case RIGHT:
                return Animations.ENEMY_WALK_RIGHT.getKeyFrame(elapsedTime, true);
            case UP:
                return Animations.ENEMY_WALK_UP.getKeyFrame(elapsedTime, true);
            case DOWN:
                default:
                return Animations.ENEMY_WALK_DOWN.getKeyFrame(elapsedTime, true);
        }
    }

    /**
     * @return The current x-position of this enemy (from the body).
     */
    @Override
    public float getX() {
        if (body == null) {
            throw new IllegalStateException("Enemy body has been destroyed, but the object is still in use.");
        }
        return body.getPosition().x;
    }

    /**
     * @return The current y-position of this enemy (from the body).
     */
    @Override
    public float getY() {
        return body.getPosition().y;
    }
}
