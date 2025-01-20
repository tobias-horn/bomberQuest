package de.tum.cit.ase.bomberquest.objects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;
import de.tum.cit.ase.bomberquest.textures.Animations;
import de.tum.cit.ase.bomberquest.textures.Drawable;

import java.awt.*;

/**
 * Represents the player character in the game.
 * The player inherits common functionality from GameObject and adds player-specific behaviors.
 */
public class Player extends GameObject implements Drawable {

    /** Time elapsed for animation purposes. */
    private float elapsedTime;
    // Fields in the Player class
    private PlayerDirection currentDirection = PlayerDirection.DOWN;
    private float vx = 0, vy = 0; // Velocity components

    // player directions
    public enum PlayerDirection {
        UP, DOWN, LEFT, RIGHT, IDLE
    }

    /**
     * Constructs a Player object at the specified tile coordinates.
     *
     * @param world The Box2D world to create the player in.
     * @param x The tile X-coordinate.
     * @param y The tile Y-coordinate.
     */
    public Player(World world, float x, float y) {
        super(world, x, y); // Call the GameObject constructor
        this.elapsedTime = 0; // Initialize elapsed time for animation
    }

    /**
     * Overrides the default hitbox creation to make the player's hitbox circular.
     *
     * @param world The Box2D world to create the hitbox in.
     * @param tileX The tile X-coordinate.
     * @param tileY The tile Y-coordinate.
     */
    @Override
    protected void createHitbox(World world, float tileX, float tileY) {
        // 1) Define the body as Dynamic so the player moves.
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(tileX + 0.5f, tileY + 0.5f); // center of the tile
        body = world.createBody(bodyDef);

        CircleShape circle = new CircleShape();
        circle.setRadius(0.4f);


        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;

        body.createFixture(fixtureDef);
        circle.dispose();

        // Prevent rotation so the player stays upright if you want.
        body.setFixedRotation(true);

        // Associate this Player object with the Box2D body:
        body.setUserData(this); // Link the player object to its body
    }

    /**
     * Updates the player's state every frame, such as animation timing.
     *
     * @param frameTime The time elapsed since the last frame.
     */
    public void tick(float frameTime) {
        this.elapsedTime += frameTime; // Update animation timing
        // Add other player-specific updates here (e.g., power-ups, health checks)
    }

    public void updateDirection(float vx, float vy) {
        this.vx = vx;
        this.vy = vy;

        // If there is no movement, set currentDirection to IDLE
        if (vx == 0 && vy == 0) {
            currentDirection = PlayerDirection.IDLE;
            return;
        }

        if (Math.abs(vx) > Math.abs(vy)) {
            currentDirection = (vx > 0) ? PlayerDirection.RIGHT : PlayerDirection.LEFT;
        } else {
            currentDirection = (vy > 0) ? PlayerDirection.UP : PlayerDirection.DOWN;
        }
    }

    public void update(float deltaTime) {
        this.elapsedTime += deltaTime;
    }

    /**
     * Provides the current appearance of the player based on its state and animation.
     *
     * @return The texture region to render for the player.
     */
    @Override
    public TextureRegion getCurrentAppearance() {
        switch (currentDirection) {
            case UP:
                return Animations.CHARACTER_WALK_UP.getKeyFrame(elapsedTime, true);
            case DOWN:
                return Animations.CHARACTER_WALK_DOWN.getKeyFrame(elapsedTime, true);
            case LEFT:
                return Animations.CHARACTER_WALK_LEFT.getKeyFrame(elapsedTime, true);
            case RIGHT:
                return Animations.CHARACTER_WALK_RIGHT.getKeyFrame(elapsedTime, true);
            case IDLE:
                return Animations.CHARACTER_IDLE.getKeyFrame(elapsedTime, true);
            default:
                throw new IllegalStateException("Unexpected direction: " + currentDirection);
        }
    }
}
