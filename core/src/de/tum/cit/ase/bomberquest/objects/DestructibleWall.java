package de.tum.cit.ase.bomberquest.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.World;
import de.tum.cit.ase.bomberquest.bonusFeatures.Score;
import de.tum.cit.ase.bomberquest.textures.Animations;
import de.tum.cit.ase.bomberquest.textures.Drawable;
import de.tum.cit.ase.bomberquest.textures.Textures;

public class DestructibleWall extends GameObject implements Drawable {

    private boolean exitUnderneath = false;
    private PowerUpType powerUpUnderneath = null;
    private boolean exitOpen = false;


    // Animation-related fields
    private boolean fadingAway = false;
    private float animationTime = Animations.FALL_OF_THE_WALL.getAnimationDuration();

    public DestructibleWall(World world, float x, float y, boolean exitUnderneath, PowerUpType powerUpUnderneath) {
        super(world, x, y);
        this.exitUnderneath = exitUnderneath;
        this.powerUpUnderneath = powerUpUnderneath;

    }

    public boolean isExitUnderneath() {
        return exitUnderneath;
    }

    public void setExitUnderneath(boolean exitUnderneath) {
        this.exitUnderneath = exitUnderneath;
    }

    public PowerUpType getPowerUpUnderneath() {
        return powerUpUnderneath;
    }

    public boolean isExitOpen() {
        return exitOpen;
    }

    @Override
    public TextureRegion getCurrentAppearance() {
        if (fadingAway) {
            TextureRegion frame = Animations.FALL_OF_THE_WALL.getKeyFrame(animationTime, false);
            Gdx.app.log("DestructibleWall", "Animation Time: " + animationTime + ", Frame: " + frame);
            return frame;
        }
        return Textures.DESTRUCTABLEWALL;
    }

    public void startFading() {
        fadingAway = true;
        animationTime = 0f;

        if (body != null) {
            body.getWorld().destroyBody(body);
            body = null;
        }
        Gdx.app.log("DestructibleWall", "Wall is now fading and its body is destroyed.");
    }

    public boolean isFadedAway() {
        return fadingAway && Animations.FALL_OF_THE_WALL.isAnimationFinished(animationTime);
    }

    public void update(float deltaTime) {
        if (fadingAway) {
            animationTime += deltaTime;

            // Destroy the body as soon as the fade completes
            if (Animations.FALL_OF_THE_WALL.isAnimationFinished(animationTime) && body != null) {
                body.getWorld().destroyBody(body);
                body = null;
            }
        }
    }
}