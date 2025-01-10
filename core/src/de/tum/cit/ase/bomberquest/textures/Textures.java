package de.tum.cit.ase.bomberquest.textures;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Contains all texture constants used in the game.
 * It is good practice to keep all textures and animations in constants to avoid loading them multiple times.
 * These can be referenced anywhere they are needed.
 */
public class Textures {

    // Game assets
    public static final TextureRegion INDESTRUCTABLEWALL = SpriteSheet.TILES.at(7, 2);
    public static final TextureRegion DESTRUCTABLEWALL = SpriteSheet.TILES.at(7, 1);
    public static final TextureRegion ENTRANCE = SpriteSheet.TILES.at(8, 3);
    public static final TextureRegion EXIT = SpriteSheet.TILES.at(9, 3);
    public static final TextureRegion BACKGROUND = SpriteSheet.TILES.at(7, 4);
    public static final TextureRegion BORDERWALL = SpriteSheet.TILES.at(9, 3);
    public static final TextureRegion POWERUP_CONCURRENTBOMB = SpriteSheet.TILES.at(8, 4);
    public static final TextureRegion BLASTRADIUS = SpriteSheet.TILES.at(8, 5);

    // Menu button textures
    public static final TextureRegion BUTTON_LONG_OFF = new TextureRegion(
            new Texture(new FileHandle("assets/menu/button_long_off.png")));
    public static final TextureRegion BUTTON_LONG_HOVER = new TextureRegion(
            new Texture(new FileHandle("assets/menu/button_long_hover.png")));

    // left = 4, right = 4, top = 4, bottom = 6
    public static final NinePatch BUTTON_LONG_NINEPATCH_OFF =
            new NinePatch(BUTTON_LONG_OFF, 4, 4, 4, 6);
    public static final NinePatch BUTTON_LONG_NINEPATCH_HOVER =
            new NinePatch(BUTTON_LONG_HOVER, 4, 4, 4, 6);
}
