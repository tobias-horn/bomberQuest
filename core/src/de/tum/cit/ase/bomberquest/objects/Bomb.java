package de.tum.cit.ase.bomberquest.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.World;
import de.tum.cit.ase.bomberquest.textures.Animations;
import de.tum.cit.ase.bomberquest.textures.Drawable;

public class Bomb extends GameObject implements Drawable {
    private float remainingTime; // Countdown (seconds)
    private float radius; // Explosion radius
    private boolean hasExploded; // Explosion state
    private float elapsedTime;

    public Bomb(World world, float tileX, float tileY, float radius) {
        super(world, tileX, tileY);
        this.radius = radius;
        this.hasExploded = false;
        this.remainingTime = 0f;
        this.elapsedTime = 0;
    }

    public void update(float deltaTime) {
        layBomb();
        if (remainingTime > 0) { // Count down if the timer is active
            remainingTime -= deltaTime; // Subtract time passed since last frame
            if (remainingTime <= 0) {
                remainingTime = 0; // Clamp to 0
                explode(); // Trigger the explosion
            }
        }
    }

    public void layBomb(){
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            startTimer();
        }
    }

    public void startTimer() {
        if (hasExploded) return; // If already exploded, do nothing
        remainingTime = 3.0f; // Start the 3-second countdown
    }

    public void explode() {
        if (hasExploded) return; // Avoid double explosions
        hasExploded = true;
        affectArea();
        // TODO: explosion visuals, sounds, etc.
    }

    public void affectArea() {
        // TODO: affected objects like walls and enemies
    }

    @Override
    public TextureRegion getCurrentAppearance() {
        return Animations.BOMB_GOING_OFF.getKeyFrame(this.elapsedTime, true); // Example animation
    }

    // Getters and Setters
    public float getRadius() { return radius; }
    public void setRadius(float radius) { this.radius = radius; }
}
