package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.World;
import de.tum.cit.ase.bomberquest.texture.Animations;
import de.tum.cit.ase.bomberquest.texture.Drawable;

/**
 * Represents an enemy in the game.
 */
public class Enemy extends GameObject implements Drawable {

    /** The elapsed time since the game started, used for animations. */
    private float elapsedTime;

    /** The movement speed of the enemy. */
    private final float speed = 2.0f;

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
    public Enemy(World world, float x, float y) {
        super(world, x, y); // Call the GameObject constructor to initialize the hitbox.
        this.elapsedTime = 0;
        setDynamicBody(); // Set the body type to dynamic since enemies move.
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

        // Enemy movement pattern: Alternate between horizontal and vertical movement.
        // Use elapsedTime to decide direction changes.
        float vx = 0; // Default no horizontal movement
        float vy = 0; // Default no vertical movement

        // Alternate movement directions based on elapsed time
        if ((int) (elapsedTime % 4) < 2) { // First 2 seconds: Horizontal movement
            if (currentDirection == Direction.LEFT || currentDirection == Direction.RIGHT) {
                vx = (currentDirection == Direction.LEFT ? -speed : speed);
            } else {
                currentDirection = Direction.LEFT; // Default to LEFT if no horizontal direction is set
                vx = -speed;
            }
        } else { // Next 2 seconds: Vertical movement
            if (currentDirection == Direction.UP || currentDirection == Direction.DOWN) {
                vy = (currentDirection == Direction.UP ? speed : -speed);
            } else {
                currentDirection = Direction.UP; // Default to UP if no vertical direction is set
                vy = speed;
            }
        }

        // Set the velocity for the enemy
        body.setLinearVelocity(new Vector2(vx, vy));

        // Update currentDirection for animation based on velocity
        if (vx < 0) {
            currentDirection = Direction.LEFT;
        } else if (vx > 0) {
            currentDirection = Direction.RIGHT;
        } else if (vy > 0) {
            currentDirection = Direction.UP;
        } else if (vy < 0) {
            currentDirection = Direction.DOWN;
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
