package de.tum.cit.ase.bomberquest.objects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import de.tum.cit.ase.bomberquest.textures.Drawable;
import de.tum.cit.ase.bomberquest.textures.Textures;

/**
 * Represents the entrance object in the game, allowing players to enter a new level or area.
 */
public class Entrance extends GameObject implements Drawable {

    /**
     * Constructs an Entrance object at the specified position within the given physics world.
     *
     * @param world the physics world where the entrance exists
     * @param x     the x-coordinate of the entrance's position
     * @param y     the y-coordinate of the entrance's position
     */
    public Entrance(World world, float x, float y) {
        super(world, x, y);
    }

    /**
     * Initializes the hitbox for the entrance, defining its physical properties.
     *
     * @param world  the physics world
     * @param tileX  the x-coordinate tile position
     * @param tileY  the y-coordinate tile position
     */
    @Override
    protected void createHitbox(World world, float tileX, float tileY) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody; // Static body for the Entrance.
        bodyDef.position.set(tileX + 0.5f, tileY + 0.5f); // Center the Entrance in its tile.

        body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.5f, 0.5f); // 1x1 collision box.

        Fixture fixture = body.createFixture(shape, 1.0f);
        fixture.setSensor(true); // Mark the hitbox as a sensor to allow movement through it.

        shape.dispose();
        body.setUserData(this);
    }


    @Override
    public TextureRegion getCurrentAppearance() {
        return Textures.ENTRANCE;
    }

}
