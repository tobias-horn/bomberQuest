package de.tum.cit.ase.bomberquest.objects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;
import de.tum.cit.ase.bomberquest.map.GameMap;
import de.tum.cit.ase.bomberquest.textures.Animations;
import de.tum.cit.ase.bomberquest.textures.Drawable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

    private List<Arrow> activeArrows = new ArrayList<>();
    private boolean canShootArrows = false; // Indicates if the player can shoot arrows

    // -- SPEED Power-Up Handling --
    private float speedMultiplier = 1.0f;
    private float speedTimer = 0f;

    // -- ARROW Power-Up Handling --
    private float arrowTimer = 0f;  // Tracks remaining Arrow Power-Up time

    // player directions
    public enum PlayerDirection {
        UP, DOWN, LEFT, RIGHT, IDLE
    }

    /**
     * Constructs a Player object at the specified tile coordinates.
     *
     * @param world The Box2D world to create the player in.
     * @param x     The tile X-coordinate.
     * @param y     The tile Y-coordinate.
     */
    public Player(World world, float x, float y) {
        super(world, x, y); // Call the GameObject constructor
        this.elapsedTime = 0; // Initialize elapsed time for animation
    }

    /**
     * Overrides the default hitbox creation to make the player's hitbox circular.
     *
     * @param world  The Box2D world to create the hitbox in.
     * @param tileX  The tile X-coordinate.
     * @param tileY  The tile Y-coordinate.
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

        body.setFixedRotation(true);

        // Associate this Player object with the Box2D body:
        body.setUserData(this); // Link the player object to its body
    }

    /**
     * Updates the player's logic each frame (including animation timing).
     *
     * @param frameTime The time elapsed since the last frame.
     */
    public void tick(float frameTime) {
        this.elapsedTime += frameTime;
    }

    /**
     * Sets the local velocity components and updates the player's facing direction.
     *
     * @param vx horizontal velocity component
     * @param vy vertical velocity component
     */
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

    /**
     * Updates all active arrows, removing them when they should be destroyed.
     *
     * @param deltaTime The time elapsed since the last frame.
     */
    public void updateArrows(float deltaTime) {
        Iterator<Arrow> it = activeArrows.iterator();
        while (it.hasNext()) {
            Arrow arrow = it.next();
            arrow.update(deltaTime);
            if (arrow.shouldRemove()) {
                arrow.destroyBody();
                it.remove();
            }
        }
    }

    /**
     * The main update method called every frame.
     * Handles the speed and arrow power-up timers and applies the final velocity to the Box2D body.
     *
     * @param deltaTime The time elapsed since the last frame.
     */
    public void update(float deltaTime) {
        this.elapsedTime += deltaTime;

        // -- Handle SPEED power-up timer --
        if (speedTimer > 0f) {
            speedTimer -= deltaTime;
            if (speedTimer <= 0f) {
                speedMultiplier = 1.0f;
                speedTimer = 0f;
            }
        }

        // -- Handle ARROW power-up timer --
        if (arrowTimer > 0f) {
            arrowTimer -= deltaTime;
            if (arrowTimer <= 0f) {
                arrowTimer = 0f;
                canShootArrows = false;
                System.out.println("update: Arrow Power-Up ended. Can no longer shoot arrows.");
            }
        }

        // Apply velocity to player's body
        if (body != null) {
            body.setLinearVelocity(vx * speedMultiplier, vy * speedMultiplier);
        }
    }

    /**
     * Activates a speed power-up, doubling the player's speed for a given duration.
     *
     * @param durationSeconds The duration (in seconds) for the speed boost.
     */
    public void activateSpeedPowerUp(float durationSeconds) {
        this.speedMultiplier = 2f;
        this.speedTimer = durationSeconds;
    }

    /**
     * Activates the Arrow Power-Up, allowing the player to shoot arrows for a limited duration.
     *
     * @param durationSeconds The duration (in seconds) for the arrow shooting ability.
     */
    public void activateArrowPowerUp(float durationSeconds) {
        this.arrowTimer = durationSeconds;
        this.canShootArrows = true;
        System.out.println("[activateArrowPowerUp] Player can now shoot arrows for " + durationSeconds + " seconds.");
    }

    /**
     * Enables permanent arrow shooting without a duration timer LEGACY CODE
     */
    public void enableArrowShooting() {
        canShootArrows = true;
        System.out.println("[enableArrowShooting] Player can now shoot arrows (no timer).");
    }

    /**
     * Attempts to shoot an arrow in the current direction if the Arrow Power-Up is active.
     *
     * @param map The current {@link GameMap} to add the arrow to.
     */
    public void shootArrow(GameMap map) {
        System.out.println("[shootArrow] Arrow shooting method called.");

        if (!canShootArrows) {
            System.out.println("[shootArrow] Cannot shoot arrows - player does not have the arrow power-up.");
            return;
        }
        if (currentDirection == PlayerDirection.IDLE) {
            System.out.println("[shootArrow] Cannot shoot arrows - player is IDLE and not moving.");
            return;
        }
        if (map.getActiveArrows().size() >= 2) {
            System.out.println("[shootArrow] Cannot shoot arrows - maximum number of arrows already active.");
            return;
        }

        float spawnX = body.getPosition().x;
        float spawnY = body.getPosition().y;

        // Adjust spawn positions based on direction
        switch (currentDirection) {
            case RIGHT:
                spawnY -= 0.5f;
                break;
            case LEFT:
                spawnX -= 1f;
                spawnY -= 0.5f;
                break;
            case UP:
                spawnX -= 0.5f;
                spawnY += 0.5f;
                break;
            case DOWN:
                spawnX -= 0.5f;
                spawnY -= 1f;
                break;
            default:
                return;
        }

        Arrow arrow = new Arrow(map.getWorld(), spawnX, spawnY, currentDirection);
        map.addArrow(arrow);
        arrow.playSound();

        System.out.println("[shootArrow] Arrow shot in direction: " + currentDirection);
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

    // -------------------------
    // Getters for power-up timers
    // -------------------------

    /**
     * Gets the remaining duration of the Speed Power-Up.
     * @return Remaining speed power-up time in seconds.
     */
    public float getSpeedTimer() {
        return speedTimer;
    }

    /**
     * Gets the remaining duration of the Arrow Power-Up.
     * @return Remaining arrow power-up time in seconds.
     */
    public float getArrowTimer() {
        return arrowTimer;
    }
}
