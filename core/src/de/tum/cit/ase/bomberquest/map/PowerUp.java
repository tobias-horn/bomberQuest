package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.World;
import de.tum.cit.ase.bomberquest.texture.Drawable;

public class PowerUp extends GameObject implements Drawable {
    private final PowerUpType type;

    public PowerUp(World world, float x, float y, PowerUpType type) {
        super(world, x, y);
        this.type = type;
    }

    @Override
    public TextureRegion getCurrentAppearance(){
        return Textures.missing;
    }
}
