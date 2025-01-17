package de.tum.cit.ase.bomberquest.objects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.World;
import de.tum.cit.ase.bomberquest.textures.Drawable;
import de.tum.cit.ase.bomberquest.textures.Textures;

public class Exit extends GameObject implements Drawable {

    private boolean active;
    private float posX; // We'll store the center X here
    private float posY; // We'll store the center Y here

    public Exit(World world, float tileX, float tileY, boolean active) {
        super(world, tileX, tileY); // calls createHitbox(...) but we'll override it
        this.active = active;

        // Because your parent logic typically centers on tileX+0.5, tileY+0.5
        this.posX = tileX + 0.5f;
        this.posY = tileY + 0.5f;
    }

    @Override
    protected void createHitbox(World world, float tileX, float tileY) {
        // Do nothing: no body/fixture for the Exit
        this.world = world;
        this.body = null;
    }

    // >>> Override these to avoid calling body.getPosition() <<<
    @Override
    public float getX() {
        return posX;
    }

    @Override
    public float getY() {
        return posY;
    }

    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public TextureRegion getCurrentAppearance() {
        return active ? Textures.EXIT_ACTIVE : Textures.EXIT_INACTIVE;
    }
}
