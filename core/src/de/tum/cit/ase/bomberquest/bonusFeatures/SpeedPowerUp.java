package de.tum.cit.ase.bomberquest.bonusFeatures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;
import de.tum.cit.ase.bomberquest.objects.GameObject;
import de.tum.cit.ase.bomberquest.textures.Textures;

public class SpeedPowerUp extends GameObject {
    private static final Sound clickSound =
            Gdx.audio.newSound(Gdx.files.internal("assets/audio/powerUp.mp3"));

    private boolean markedForRemoval = false;

    public SpeedPowerUp(World world, float x, float y) {
        super(world, x, y);
        createHitbox(world, x, y);
    }

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

    @Override
    public TextureRegion getCurrentAppearance() {
        return Textures.SPEED_POWER_UP;
    }

    public static void playSound() {
        clickSound.play();
    }

    public boolean isMarkedForRemoval() {
        return markedForRemoval;
    }

    public void markForRemoval() {
        this.markedForRemoval = true;
    }
}
