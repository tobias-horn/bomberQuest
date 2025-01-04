package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.World;
import de.tum.cit.ase.bomberquest.texture.Drawable;
import de.tum.cit.ase.bomberquest.texture.Textures;

public class Entrance extends GameObject implements Drawable {
    public Entrance(World world, float x, float y) {
        super(world, x, y);
    }

    @Override
    public TextureRegion getCurrentAppearance() {
        return Textures.ENTRANCE;
    }

}
