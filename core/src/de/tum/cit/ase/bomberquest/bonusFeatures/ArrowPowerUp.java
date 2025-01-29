package de.tum.cit.ase.bomberquest.bonusFeatures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;
import de.tum.cit.ase.bomberquest.objects.GameObject;
import de.tum.cit.ase.bomberquest.textures.Drawable;
import de.tum.cit.ase.bomberquest.textures.Textures;

/**
 * Represents an arrow power-up within the game.
 * When collected, it provides the player with enhanced arrow capabilities.
 */
public class ArrowPowerUp extends GameObject implements Drawable {

    /**
     * The sound played when the power-up is collected.
     */
    private static final Sound clickSound =
            Gdx.audio.newSound(Gdx.files.internal("assets/audio/powerUp.mp3"));

    /**
     * Indicates whether the power-up is marked for removal from the game.
     */
    private boolean markedForRemoval = false;

    /**
     * Constructs an ArrowPowerUp at the specified position within the given physics world.
     *
     * @param world the physics world where the power-up exists
     * @param x     the x-coordinate of the power-up's position
     * @param y     the y-coordinate of the power-up's position
     */
    public ArrowPowerUp(World world, float x, float y) {
        super(world, x, y);
        createHitbox(world, x, y);
    }

    /**
     * Initializes the hitbox for the arrow power-up, defining its physical properties.
     *
     * @param world  the physics world
     * @param tileX  the x-coordinate tile position
     * @param tileY  the y-coordinate tile position
     */
    @Override
    protected void createHitbox(World world, float tileX, float tileY) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(tileX + 0.5f, tileY + 0.5f);

        body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.5f, 0.5f);

        Fixture fixture = body.createFixture(shape, 0f);
        fixture.setSensor(true);

        shape.dispose();
        body.setUserData(this);
    }

    /**
     * Retrieves the current texture representing the arrow power-up.
     *
     * @return the texture region for the arrow power-up
     */
    @Override
    public TextureRegion getCurrentAppearance() {
        return Textures.ARROW_POWER_UP;
    }

    /**
     * Plays the sound effect associated with collecting the arrow power-up.
     */
    public static void playSound() {
        clickSound.play();
    }

    /**
     * Checks if the power-up has been marked for removal.
     *
     * @return true if the power-up is marked for removal, false otherwise
     */
    public boolean isMarkedForRemoval() {
        return markedForRemoval;
    }

    /**
     * Marks the power-up for removal from the game, scheduling it to be deleted.
     */
    public void markForRemoval() {
        this.markedForRemoval = true;
    }
}
