package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;
import de.tum.cit.ase.bomberquest.texture.Animations;
import de.tum.cit.ase.bomberquest.texture.Drawable;

public class Player implements Drawable {
    private float elapsedTime;
    private final Body hitbox;

    public Player(World world, float x, float y) {
        this.hitbox = createHitbox(world, x, y);
    }

    private Body createHitbox(World world, float startX, float startY) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        // Center the player in that tile
        bodyDef.position.set(startX + 0.5f, startY + 0.5f);

        Body body = world.createBody(bodyDef);

        // Circle radius of 0.3 => 0.6 diameter
        CircleShape circle = new CircleShape();
        circle.setRadius(0.3f);

        body.createFixture(circle, 1.0f);
        circle.dispose();

        body.setUserData(this);
        return body;
    }

    public void tick(float frameTime) {
        this.elapsedTime += frameTime;
        // Example movement logic here...
    }

    @Override
    public TextureRegion getCurrentAppearance() {
        // Example: walking-down animation
        return Animations.CHARACTER_WALK_DOWN.getKeyFrame(this.elapsedTime, true);
    }

    @Override
    public float getX() {
        return hitbox.getPosition().x;
    }

    @Override
    public float getY() {
        return hitbox.getPosition().y;
    }

    public Body getHitbox() {
        return hitbox;
    }
}
