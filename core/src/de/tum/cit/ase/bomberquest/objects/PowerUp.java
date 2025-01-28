package de.tum.cit.ase.bomberquest.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import de.tum.cit.ase.bomberquest.textures.Drawable;
import de.tum.cit.ase.bomberquest.textures.Textures;

/**
 * Represents a power-up object within the game that provides various enhancements to the player.
 * Power-ups can be collected by the player to gain benefits such as increased speed, larger blast radius, or the ability to place multiple bombs concurrently.
 */
public class PowerUp extends GameObject implements Drawable {

    private final PowerUpType type;


    private static final Sound clickSound = Gdx.audio.newSound(Gdx.files.internal("assets/audio/powerUp.mp3"));


    private boolean markedForRemoval = false;


    public void markForRemoval() {
        this.markedForRemoval = true;
    }


    public boolean isMarkedForRemoval() {
        return markedForRemoval;
    }

    /**
     * Constructs a PowerUp object at the specified position within the given physics world.
     *
     * @param world the physics world where the power-up exists
     * @param x     the x-coordinate of the power-up's position
     * @param y     the y-coordinate of the power-up's position
     * @param type  the type of power-up
     */
    public PowerUp(World world, float x, float y, PowerUpType type) {
        super(world, x, y);
        this.type = type;
    }

    /**
     * Initializes the hitbox for the power-up, defining its physical properties.
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

        // Create the fixture as a SENSOR so it doesn't block movement
        Fixture fixture = body.createFixture(shape, 0f);
        fixture.setSensor(true);

        shape.dispose();
        body.setUserData(this);
    }

    /**
     * Retrieves the current texture representing the power-up based on its type.
     *
     * @return the texture region for the current appearance of the power-up
     * @throws IllegalStateException if the power-up type is unexpected
     */
    @Override
    public TextureRegion getCurrentAppearance() {
        switch (type) {
            case CONCURRENTBOMB:
                return Textures.POWERUP_CONCURRENTBOMB;
            case BLASTRADIUS:
                return Textures.BLASTRADIUS;
            case SPEED:
                return Textures.SPEED_POWER_UP;
            case ARROW:
                return Textures.ARROW_POWER_UP;
            default:
                throw new IllegalStateException("Unexpected type: " + type);
        }
    }

    /**
     * Plays the sound effect associated with collecting the power-up.
     */
    public static void playSound() {
        clickSound.play();
    }

    /**
     * Retrieves the type of the power-up.
     *
     * @return the power-up type
     */
    public PowerUpType getType() {
        return this.type;
    }
}
