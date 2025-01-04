package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;
import de.tum.cit.ase.bomberquest.texture.Drawable;

public abstract class GameObject implements Drawable {
    protected final float x;
    protected final float y;
    protected Body body;


    public GameObject(World world, float x, float y) {
        this.x = x;
        this.y = y;
        createHitbox(world);
    }

    protected void createHitbox(World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(this.x, this.y);
        body = world.createBody(bodyDef);

        PolygonShape box = new PolygonShape();
        box.setAsBox(0.5f, 0.5f);  // Default size (1x1 tile)
        body.createFixture(box, 1.0f);  // Density
        box.dispose();

        body.setUserData(this);  // Attach object to Box2D body
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public abstract TextureRegion getCurrentAppearance();
}
