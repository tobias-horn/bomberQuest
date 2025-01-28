package de.tum.cit.ase.bomberquest.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;
import de.tum.cit.ase.bomberquest.textures.Drawable;
import de.tum.cit.ase.bomberquest.textures.Textures;

public class Arrow extends GameObject implements Drawable {
    private final Player.PlayerDirection direction;
    private final float speed = 10f;  // Arrow speed
    private float arrowLifetime = 3f;   // arrow disappears after 3 sec
    private boolean markedForRemoval = false;
    private static final Sound arrowSound = Gdx.audio.newSound(Gdx.files.internal("assets/audio/arrowSound.mp3"));

    public void markForRemoval() {
        this.markedForRemoval = true;
        System.out.println("[PowerUp] Marked for removal: " + this);
    }

    public boolean isMarkedForRemoval() {
        return this.markedForRemoval;
    }

    public Arrow(World world, float x, float y, Player.PlayerDirection direction) {
        super(world, x, y);
        this.direction = direction;
        createHitbox(world, x, y);
        setInitialVelocity();
    }

    @Override
    protected void createHitbox(World world, float tileX, float tileY) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(tileX + 0.5f, tileY + 0.5f); // Centered in tile
        bodyDef.bullet = true; // Ensures precise fast movement

        body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.2f, 0.2f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.isSensor = false;

        body.createFixture(fixtureDef);
        shape.dispose();
        body.setUserData(this);
    }

    private void setInitialVelocity() {
        if (body == null) return;
        float vx = 0, vy = 0;
        switch (direction) {
            case UP -> vy = speed;
            case DOWN -> vy = -speed;
            case LEFT -> vx = -speed;
            case RIGHT -> vx = speed;
            // If direction were IDLE, we wouldn’t create the arrow at all.
        }
        body.setLinearVelocity(vx, vy);
    }

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

    public void update(float deltaTime) {
        arrowLifetime -= deltaTime;
        if (arrowLifetime <= 0) markForRemoval();
    }

    public boolean shouldRemove() {
        return isMarkedForRemoval();
    }

    public void destroyBody() {
        if (body != null) {
            body.getWorld().destroyBody(body);
            body = null;
        }
    }

    public void playSound(){
        arrowSound.play();
    }
}