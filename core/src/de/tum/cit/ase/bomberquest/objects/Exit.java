package de.tum.cit.ase.bomberquest.objects;

import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import de.tum.cit.ase.bomberquest.textures.Textures;
import de.tum.cit.ase.bomberquest.textures.Drawable;

/**
 * Represents the exit tile. It implements Drawable so GameScreen
 * will actually render it (similar to how power-ups are rendered).
 */
public class Exit extends GameObject implements Drawable {

    private boolean active;

    public Exit(World world, float tileX, float tileY, boolean initiallyActive) {
        super(world, tileX, tileY);
        this.active = initiallyActive;

        // Turn this into a sensor so it doesn't block movement,
        // but still has a body so "obj.getBody() != null" is true.
        for (Fixture fixture : body.getFixtureList()) {
            fixture.setSensor(true);
        }
    }

    @Override
    protected void createHitbox(World world, float tileX, float tileY) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        // Center the tile in Box2D coordinates
        bodyDef.position.set(tileX + 0.5f, tileY + 0.5f);

        this.body = world.createBody(bodyDef);

        // A 1x1 tile => half-width = 0.5
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.5f, 0.5f);

        this.body.createFixture(shape, 0.0f); // 0.0f density for a static object
        shape.dispose();

        this.body.setUserData(this);
    }

    /**
     * If all enemies are dead, GameMap sets the exit active.
     * If it's active AND the player stands on it, the game is won.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    /**
     * Return the sprite to be drawn (in GameScreen).
     */
    @Override
    public TextureRegion getCurrentAppearance() {
        // The textures below come from Textures.EXIT_ACTIVE and Textures.EXIT_INACTIVE
        return active ? Textures.EXIT_ACTIVE : Textures.EXIT_INACTIVE;
    }
}