package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.World;
import de.tum.cit.ase.bomberquest.texture.Drawable;
import de.tum.cit.ase.bomberquest.texture.Textures;

public class DestructibleWall extends GameObject implements Drawable {
    private boolean exitUnderneath = false;
    private PowerUpType powerUpUnderneath = null;

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

    @Override
    public TextureRegion getCurrentAppearance() {
        return Textures.DESTRUCTABLEWALL; // Example for IndestructibleWall
    }

}
