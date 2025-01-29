package de.tum.cit.ase.bomberquest.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;
import de.tum.cit.ase.bomberquest.textures.Drawable;
import de.tum.cit.ase.bomberquest.textures.Textures;

/**
 * Represents an arrow object in the game that can be fired by the player.
 * Arrows move in a specified direction, have a limited lifetime, and can be marked for removal.
 */
public class Arrow extends GameObject implements Drawable {

    /**
     * The direction in which the arrow is moving.
     */
    private final Player.PlayerDirection direction;

    /**
     * The speed at which the arrow travels.
     */
    private final float speed = 10f;  // Arrow speed

    /**
     * The remaining lifetime of the arrow before it disappears.
     */
    private float arrowLifetime = 3f;   // arrow disappears after 3 sec

    /**
     * Indicates whether the arrow is marked for removal from the game.
     */
    private boolean markedForRemoval = false;

    /**
     * The sound played when the arrow is fired.
     */
    private static final Sound arrowSound = Gdx.audio.newSound(Gdx.files.internal("assets/audio/arrowSound.mp3"));

    /**
     * Marks the arrow for removal from the game, scheduling it to be deleted
     */
    public void markForRemoval() {
        this.markedForRemoval = true;
        System.out.println("[PowerUp] Marked for removal: " + this);
    }

    /**
     * Checks if the arrow has been marked for removal.
     *
     * @return true if the arrow is marked for removal, false otherwise
     */
    public boolean isMarkedForRemoval() {
        return this.markedForRemoval;
    }

    /**
     * Constructs an Arrow object at the specified position within the given physics world and direction.
     *
     * @param world     the physics world where the arrow exists
     * @param x         the x-coordinate of the arrow's position
     * @param y         the y-coordinate of the arrow's position
     * @param direction the direction in which the arrow will travel
     */
    public Arrow(World world, float x, float y, Player.PlayerDirection direction) {
        super(world, x, y);
        this.direction = direction;
        createHitbox(world, x, y);
        setInitialVelocity();
    }

    /**
     * Initializes the hitbox for the arrow, defining its physical properties.
     *
     * @param world  the physics world
     * @param tileX  the x-coordinate tile position
     * @param tileY  the y-coordinate tile position
     */
    @Override
    protected void createHitbox(World world, float tileX, float tileY) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(tileX + 0.5f, tileY + 0.5f); // Centered in tile
        bodyDef.bullet = true; // Ensures precise fast movement

        body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.08f, 0.016f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.isSensor = false;

        body.createFixture(fixtureDef);
        shape.dispose();
        body.setUserData(this);
    }

    /**
     * Sets the initial velocity of the arrow based on its direction.
     * The velocity determines the movement speed and direction of the arrow in the physics world.
     */
    private void setInitialVelocity() {
        if (body == null) return;
        float vx = 0, vy = 0;
        switch (direction) {
            case UP -> vy = speed;
            case DOWN -> vy = -speed;
            case LEFT -> vx = -speed;
            case RIGHT -> vx = speed;

        }
        body.setLinearVelocity(vx, vy);
    }

    /**
     * Retrieves the current texture representing the arrow based on its direction.
     *
     * @return the texture region for the current appearance of the arrow
     */
    @Override
    public TextureRegion getCurrentAppearance() {
        return switch (direction) {
            case UP -> Textures.ARROW_UP;
            case DOWN -> Textures.ARROW_DOWN;
            case LEFT -> Textures.ARROW_LEFT;
            case RIGHT -> Textures.ARROW_RIGHT;
            default -> Textures.ARROW_RIGHT; // Fallback
        };
    }

    /**
     * Updates the state of the arrow based on the elapsed time.
     * Decreases the arrow's lifetime and marks it for removal if its lifetime has expired.
     *
     * @param deltaTime the time elapsed since the last update
     */
    public void update(float deltaTime) {
        arrowLifetime -= deltaTime;
        if (arrowLifetime <= 0) markForRemoval();
    }

    /**
     * Determines whether the arrow should be removed from the game.
     *
     * @return true if the arrow is marked for removal, false otherwise
     */
    public boolean shouldRemove() {
        return isMarkedForRemoval();
    }

    /**
     * Destroys the physical body of the arrow within the physics world.
     * This effectively removes the arrow from the simulation.
     */
    public void destroyBody() {
        if (body != null) {
            body.getWorld().destroyBody(body);
            body = null;
        }
    }

    /**
     * Plays the sound effect associated with firing the arrow.
     */
    public void playSound(){
        arrowSound.play();
    }
}
