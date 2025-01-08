package de.tum.cit.ase.bomberquest.texture;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Contains all animation constants used in the game.
 * It is good practice to keep all textures and animations in constants to avoid loading them multiple times.
 * These can be referenced anywhere they are needed.
 */
public class Animations {
    
    /**
     * The animation for the character walking down.
     */
    public static final Animation<TextureRegion> CHARACTER_WALK_DOWN = new Animation<>(0.1f,
            SpriteSheet.CHARACTER.at(1, 1),
            SpriteSheet.CHARACTER.at(1, 2),
            SpriteSheet.CHARACTER.at(1, 3),
            SpriteSheet.CHARACTER.at(1, 4)
    );

    public static final Animation<TextureRegion> ENEMY_WALK_LEFT = new Animation<>(0.1f,
            SpriteSheet.ENEMY.at(16, 4),
            SpriteSheet.ENEMY.at(16, 5),
            SpriteSheet.ENEMY.at(16, 6)
    );

    public static final Animation<TextureRegion> ENEMY_WALK_RIGHT = new Animation<>(0.1f,
            SpriteSheet.ENEMY.at(16, 1),
            SpriteSheet.ENEMY.at(16, 2),
            SpriteSheet.ENEMY.at(16, 3)
    );

    public static final Animation<TextureRegion> ENEMY_WALK_UP = new Animation<>(0.1f,
            SpriteSheet.ENEMY.at(16, 1),
            SpriteSheet.ENEMY.at(16, 2),
            SpriteSheet.ENEMY.at(16, 3)
    );

    public static final Animation<TextureRegion> ENEMY_WALK_DOWN = new Animation<>(0.1f,
            SpriteSheet.ENEMY.at(1, 4),
            SpriteSheet.ENEMY.at(2, 4),
            SpriteSheet.ENEMY.at(3, 4),
            SpriteSheet.ENEMY.at(4, 4)
    );




}
