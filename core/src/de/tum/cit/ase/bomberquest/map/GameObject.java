package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;

public abstract class GameObject {
    protected Body body; // The Box2D body for collisions

    public GameObject(World world, float tileX, float tileY) {
        createHitbox(world, tileX, tileY);
    }

    /**
     * Creates a 1x1 box fixture, centered at (tileX+0.5, tileY+0.5).
     */
    protected void createHitbox(World world, float tileX, float tileY) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        // Center this tile in Box2D
        bodyDef.position.set(tileX + 0.5f, tileY + 0.5f);

        body = world.createBody(bodyDef);

        // A 1x1 tile => half-width = 0.5
        PolygonShape box = new PolygonShape();
        box.setAsBox(0.5f, 0.5f);
        body.createFixture(box, 1.0f);
        box.dispose();

        body.setUserData(this);
    }

    /**
     * @return The current x-position of this object's center (Box2D).
     */
    public float getX() {
        return body.getPosition().x;
    }

    /**
     * @return The current y-position of this object's center (Box2D).
     */
    public float getY() {
        return body.getPosition().y;
    }

    /**
     * Each concrete subclass must provide a texture.
     */
    public abstract TextureRegion getCurrentAppearance();

    public Body getBody() {
        return body;
    }
}
