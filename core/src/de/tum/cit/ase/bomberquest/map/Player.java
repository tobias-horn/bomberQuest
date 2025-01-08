package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.World;
import de.tum.cit.ase.bomberquest.texture.Animations;
import de.tum.cit.ase.bomberquest.texture.Drawable;

/**
 * Represents the player character in the game.
 * The player inherits common functionality from GameObject and adds player-specific behaviors.
 */
public class Player extends GameObject implements Drawable {

    /** Time elapsed for animation purposes. */
    private float elapsedTime;

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
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(tileX + 0.5f, tileY + 0.5f); // Center in the tile

        body = world.createBody(bodyDef);

        CircleShape circle = new CircleShape();
        circle.setRadius(0.3f); // Radius of the player's circular hitbox

        body.createFixture(circle, 1.0f);
        circle.dispose();

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

    /**
     * Provides the current appearance of the player based on its state and animation.
     *
     * @return The texture region to render for the player.
     */
    @Override
    public TextureRegion getCurrentAppearance() {
        return Animations.CHARACTER_WALK_DOWN.getKeyFrame(this.elapsedTime, true); // Example animation
    }
}
