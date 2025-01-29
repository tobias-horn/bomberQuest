package de.tum.cit.ase.bomberquest.textures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Contains all texture constants used in the game.
 * These can be referenced anywhere they are needed.
 */
public class Textures {

    public static final Texture LOGO = new Texture(Gdx.files.internal("assets/menu/bomberQuestLogo.png"));


    // Game assets
    public static final TextureRegion INDESTRUCTABLEWALL = SpriteSheet.TILES.at(7, 2);

    public static final TextureRegion TLBW = SpriteSheet.TILES.at(1, 1);
    public static final TextureRegion TRBW = SpriteSheet.TILES.at(1, 3);
    public static final TextureRegion BLBW = SpriteSheet.TILES.at(3, 1);
    public static final TextureRegion BRBW = SpriteSheet.TILES.at(3, 3);

    public static final TextureRegion TBW = SpriteSheet.TILES.at(1, 2);
    public static final TextureRegion BBW = SpriteSheet.TILES.at(3, 2);
    public static final TextureRegion RBW = SpriteSheet.TILES.at(2, 3);
    public static final TextureRegion LBW = SpriteSheet.TILES.at(2, 1);

    public static final TextureRegion DESTRUCTABLEWALL = SpriteSheet.TILES.at(7, 1);
    public static final TextureRegion ENTRANCE = SpriteSheet.TILES.at(8, 3);
    public static final TextureRegion EXIT_INACTIVE = SpriteSheet.TILES.at(9, 2);
    public static final TextureRegion EXIT_ACTIVE = SpriteSheet.TILES.at(9, 3);
    public static final TextureRegion BACKGROUND = SpriteSheet.TILES.at(7, 4);
    public static final TextureRegion BORDERWALL = SpriteSheet.TILES.at(9, 3);
    public static final TextureRegion BLASTRADIUS = new TextureRegion(
            new Texture(new FileHandle("assets/texture/blastRadiusPU.png")));

    public static final TextureRegion POWERUP_CONCURRENTBOMB = new TextureRegion(
            new Texture(new FileHandle("assets/texture/concurrentBombPU.png")));

    public static final TextureRegion BLASTRADIOUS_HUD = new TextureRegion(
            new Texture(new FileHandle("assets/texture/blastRadiusBombPuHUD.png")));

    public static final TextureRegion CONCURRENTBOMB_HUD = new TextureRegion(
            new Texture(new FileHandle("assets/texture/concurrentBombPuHUD.png")));

    public static final TextureRegion ENEMYCOUNT_HUD = new TextureRegion(new Texture(new FileHandle("assets/texture/enemyCountHUD.png")));

    public static final TextureRegion SPEED_POWER_UP_HUD = new TextureRegion(new Texture(new FileHandle("assets/texture/speedPuHUD.png")));
    public static final TextureRegion SPEED_POWER_UP = new TextureRegion(new Texture(new FileHandle("assets/texture/speedPU.png")));

    public static final TextureRegion ARROW_POWER_UP = new TextureRegion(new Texture(new FileHandle("assets/texture/arrowPowerUp.png")));
    public static final TextureRegion ARROW_POWER_UP_HUD = new TextureRegion(new Texture(new FileHandle("assets/texture/arrowPowerUpHUD.png")));

    public static final TextureRegion ARROW_RIGHT = new TextureRegion(new Texture(new FileHandle("assets/texture/arrowRight.png")));
    public static final TextureRegion ARROW_LEFT = new TextureRegion(new Texture(new FileHandle("assets/texture/arrowLeft.png")));
    public static final TextureRegion ARROW_UP = new TextureRegion(new Texture(new FileHandle("assets/texture/arrowUp.png")));
    public static final TextureRegion ARROW_DOWN = new TextureRegion(new Texture(new FileHandle("assets/texture/arrowDown.png")));


    // Menu button textures
    public static final TextureRegion BUTTON_LONG_OFF = new TextureRegion(
            new Texture(new FileHandle("assets/menu/button_long_off.png")));
    public static final TextureRegion BUTTON_LONG_HOVER = new TextureRegion(
            new Texture(new FileHandle("assets/menu/button_long_hover.png")));

    // Nine patch custom button. This defines the stretchable area of the button texture
    // left = 4, right = 4, top = 4, bottom = 6
    public static final NinePatch BUTTON_LONG_NINEPATCH_OFF =
            new NinePatch(BUTTON_LONG_OFF, 4, 4, 4, 6);
    public static final NinePatch BUTTON_LONG_NINEPATCH_HOVER =
            new NinePatch(BUTTON_LONG_HOVER, 4, 4, 4, 6);
}
