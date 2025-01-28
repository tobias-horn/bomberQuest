package de.tum.cit.ase.bomberquest.objects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.World;
import de.tum.cit.ase.bomberquest.textures.Drawable;
import de.tum.cit.ase.bomberquest.textures.Textures;

/**
 * Represents an indestructible wall within the game.
 * Indestructible walls cannot be destroyed by explosions or other destructive actions.
 * They can serve as barriers or boundaries within the game map.
 */
public class IndestructibleWall extends GameObject implements Drawable {

    private boolean borderWall;

    /**
     * Constructs an IndestructibleWall at the specified position within the given physics world.
     *
     * @param world the physics world where the wall exists
     * @param x     the x-coordinate of the wall's position
     * @param y     the y-coordinate of the wall's position
     */
    public IndestructibleWall(World world, float x, float y) {
        super(world, x, y);
    }

    /**
     * Retrieves the current texture representing the indestructible wall.
     * If the wall is marked as a border wall, a different texture is returned.
     *
     * @return the texture region for the indestructible wall
     */
    @Override
    public TextureRegion getCurrentAppearance() {
        if (!isBorderWall()) {
            return Textures.INDESTRUCTABLEWALL;
        } else {
            return Textures.BORDERWALL;
        }
    }


    public boolean isBorderWall() {
        return borderWall;
    }


    public void setBorderWall(boolean borderWall) {
        this.borderWall = borderWall;
    }
}
