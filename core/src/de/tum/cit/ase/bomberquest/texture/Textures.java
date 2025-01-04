package de.tum.cit.ase.bomberquest.texture;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Contains all texture constants used in the game.
 * It is good practice to keep all textures and animations in constants to avoid loading them multiple times.
 * These can be referenced anywhere they are needed.
 */
public class Textures {
    
    public static final TextureRegion INDESTRUCTABLEWALL = SpriteSheet.TILES.at(7, 2);

    public static final TextureRegion DESTRUCTABLEWALL = SpriteSheet.TILES.at(7, 3);

    public static final TextureRegion ENTRANCE = SpriteSheet.TILES.at(8, 3);

    public static final TextureRegion EXIT = SpriteSheet.TILES.at(9, 3);

    public static final TextureRegion POWERUP_CONCURRENTBOMB = SpriteSheet.TILES.at(8, 3);

    public static final TextureRegion BLASTRADIUS = SpriteSheet.TILES.at(8, 3);
    
}
