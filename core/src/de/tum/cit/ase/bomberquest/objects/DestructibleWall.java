package de.tum.cit.ase.bomberquest.objects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.World;
import de.tum.cit.ase.bomberquest.textures.Drawable;
import de.tum.cit.ase.bomberquest.textures.Textures;

public class DestructibleWall extends GameObject implements Drawable {

    private boolean exitUnderneath;
    private PowerUpType powerUpUnderneath;
    private boolean exitOpen;

    /**
     * Constructs a DestructibleWall object.
     *
     * @param world               The Box2D world.
     * @param x                   The x-coordinate of the wall.
     * @param y                   The y-coordinate of the wall.
     * @param exitUnderneath      Indicates if an exit is underneath the wall.
     * @param powerUpUnderneath   The power-up type underneath the wall, if any.
     */
    public DestructibleWall(World world, float x, float y, boolean exitUnderneath, PowerUpType powerUpUnderneath) {
        super(world, x, y);
        this.exitUnderneath = exitUnderneath;
        this.powerUpUnderneath = powerUpUnderneath;
        this.exitOpen = false;
    }

    /**
     * Checks if there is an exit underneath the wall.
     *
     * @return {@code true} if an exit is underneath, {@code false} otherwise.
     */
    public boolean isExitUnderneath() {
        return exitUnderneath;
    }

    /**
     * Sets whether there is an exit underneath the wall.
     *
     * @param exitUnderneath {@code true} to indicate an exit underneath, {@code false} otherwise.
     */
    public void setExitUnderneath(boolean exitUnderneath) {
        this.exitUnderneath = exitUnderneath;
    }

    /**
     * Retrieves the power-up type underneath the wall.
     *
     * @return The {@link PowerUpType} underneath, or {@code null} if none.
     */
    public PowerUpType getPowerUpUnderneath() {
        return powerUpUnderneath;
    }

    /**
     * Checks if the exit is open.
     *
     * @return {@code true} if the exit is open, {@code false} otherwise.
     */
    public boolean isExitOpen() {
        return exitOpen;
    }

    /**
     * Sets the exit state.
     *
     * @param exitOpen {@code true} to open the exit, {@code false} to close it.
     */
    public void setExitOpen(boolean exitOpen) {
        this.exitOpen = exitOpen;
    }

    /**
     * Provides the current appearance of the destructible wall.
     *
     * @return The texture region representing the destructible wall.
     */
    @Override
    public TextureRegion getCurrentAppearance() {
        return Textures.DESTRUCTABLEWALL;
    }
}
