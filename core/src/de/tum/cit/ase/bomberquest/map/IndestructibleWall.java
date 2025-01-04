package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.World;
import de.tum.cit.ase.bomberquest.texture.Drawable;
import de.tum.cit.ase.bomberquest.texture.Textures;

public class IndestructibleWall extends GameObject implements Drawable {

    private boolean borderWall;


    public IndestructibleWall(World world, float x, float y) {
        super(world, x, y);
    }



    @Override
    public TextureRegion getCurrentAppearance() {
        if(!isBorderWall()) {
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
