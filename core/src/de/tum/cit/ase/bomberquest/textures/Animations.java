package de.tum.cit.ase.bomberquest.textures;

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





    // CHARACTER WALKING UP
    public static final Animation<TextureRegion> CHARACTER_WALK_UP = new Animation<>(0.1f,
            SpriteSheet.CHARACTER.at(1, 1),
            SpriteSheet.CHARACTER.at(1, 2),
            SpriteSheet.CHARACTER.at(1, 3),
            SpriteSheet.CHARACTER.at(1, 4)
    );

    // CHARACTER WALKING DOWN
    public static final Animation<TextureRegion> CHARACTER_WALK_DOWN = new Animation<>(0.1f,
            SpriteSheet.CHARACTER.at(1, 1),
            SpriteSheet.CHARACTER.at(1, 2),
            SpriteSheet.CHARACTER.at(1, 3),
            SpriteSheet.CHARACTER.at(1, 4)
    );

    // CHARACTER WALKING RIGHT
    public static final Animation<TextureRegion> CHARACTER_WALK_RIGHT = new Animation<>(0.1f,
            SpriteSheet.CHARACTER.at(1, 1),
            SpriteSheet.CHARACTER.at(1, 2),
            SpriteSheet.CHARACTER.at(1, 3),
            SpriteSheet.CHARACTER.at(1, 4)
    );

    // CHARACTER WALKING LEFT
    public static final Animation<TextureRegion> CHARACTER_WALK_LEFT = new Animation<>(0.1f,
            SpriteSheet.CHARACTER.at(1, 1),
            SpriteSheet.CHARACTER.at(1, 2),
            SpriteSheet.CHARACTER.at(1, 3),
            SpriteSheet.CHARACTER.at(1, 4)
    );





    // ENEMY WALKING UP
    public static final Animation<TextureRegion> ENEMY_WALK_UP = new Animation<>(0.1f,
            SpriteSheet.ENEMY.at(16, 1),
            SpriteSheet.ENEMY.at(16, 2),
            SpriteSheet.ENEMY.at(16, 3)
    );

    // ENEMY WALKING DOWN
    public static final Animation<TextureRegion> ENEMY_WALK_DOWN = new Animation<>(0.1f,
            SpriteSheet.ENEMY.at(1, 4),
            SpriteSheet.ENEMY.at(2, 4),
            SpriteSheet.ENEMY.at(3, 4),
            SpriteSheet.ENEMY.at(4, 4)
    );

    // ENEMY WALKING RIGHT
    public static final Animation<TextureRegion> ENEMY_WALK_RIGHT = new Animation<>(0.1f,
            SpriteSheet.ENEMY.at(16, 1),
            SpriteSheet.ENEMY.at(16, 2),
            SpriteSheet.ENEMY.at(16, 3)
    );

    // ENEMY WALKING LEFT
    public static final Animation<TextureRegion> ENEMY_WALK_LEFT = new Animation<>(0.1f,
            SpriteSheet.ENEMY.at(16, 4),
            SpriteSheet.ENEMY.at(16, 5),
            SpriteSheet.ENEMY.at(16, 6)
    );

    //ENEMY DYING
    public static final Animation<TextureRegion> ENEMY_DYING = new Animation<>(0.1f,
            SpriteSheet.ENEMY.at(16, 4),
            SpriteSheet.ENEMY.at(16, 5),
            SpriteSheet.ENEMY.at(16, 6)
    );





    // Bomb Animation before explosion
    public static final Animation<TextureRegion> BOMB_GOING_OFF = new Animation<>(0.1f,
            SpriteSheet.BOMB.at(4, 3),
            SpriteSheet.BOMB.at(4, 2),
            SpriteSheet.BOMB.at(4, 1)
    );

    // Center Blast
    public static final Animation<TextureRegion> BOMB_CENTER_EXPLOSION = new Animation<>(0.1f,
            SpriteSheet.ORIGINAL_BOMBERMAN.at(7, 3),
            SpriteSheet.ORIGINAL_BOMBERMAN.at(7, 8),
            SpriteSheet.ORIGINAL_BOMBERMAN.at(12, 3),
            SpriteSheet.ORIGINAL_BOMBERMAN.at(12, 8)
    );

    // Right Blast
    public static final Animation<TextureRegion> BLAST_RIGHT_INITIAL_PIECE = new Animation<>(0.1f,
            SpriteSheet.ORIGINAL_BOMBERMAN.at(7, 4),
            SpriteSheet.ORIGINAL_BOMBERMAN.at(7, 9),
            SpriteSheet.ORIGINAL_BOMBERMAN.at(12, 4),
            SpriteSheet.ORIGINAL_BOMBERMAN.at(12, 9)
    );

    public static final Animation<TextureRegion> BLAST_RIGHT_END_PIECE = new Animation<>(0.1f,
            SpriteSheet.ORIGINAL_BOMBERMAN.at(7, 5),
            SpriteSheet.ORIGINAL_BOMBERMAN.at(7, 10),
            SpriteSheet.ORIGINAL_BOMBERMAN.at(7, 5),
            SpriteSheet.ORIGINAL_BOMBERMAN.at(7, 10)
    );

    //LEFT BLAST
    public static final Animation<TextureRegion> BLAST_LEFT_INITIAL_PIECE = new Animation<>(0.1f,
            SpriteSheet.ORIGINAL_BOMBERMAN.at(7, 4),
            SpriteSheet.ORIGINAL_BOMBERMAN.at(7, 9),
            SpriteSheet.ORIGINAL_BOMBERMAN.at(12, 4),
            SpriteSheet.ORIGINAL_BOMBERMAN.at(12, 9)
    );

    public static final Animation<TextureRegion> BLAST_LEFT_END_PIECE = new Animation<>(0.1f,
            SpriteSheet.ORIGINAL_BOMBERMAN.at(7, 5),
            SpriteSheet.ORIGINAL_BOMBERMAN.at(7, 10),
            SpriteSheet.ORIGINAL_BOMBERMAN.at(7, 5),
            SpriteSheet.ORIGINAL_BOMBERMAN.at(7, 10)
    );

    // UP BLAST
    public static final Animation<TextureRegion> BLAST_UP_INITIAL_PIECE = new Animation<>(0.1f,
            SpriteSheet.ORIGINAL_BOMBERMAN.at(7, 4),
            SpriteSheet.ORIGINAL_BOMBERMAN.at(7, 9),
            SpriteSheet.ORIGINAL_BOMBERMAN.at(12, 4),
            SpriteSheet.ORIGINAL_BOMBERMAN.at(12, 9)
    );

    public static final Animation<TextureRegion> BLAST_UP_END_PIECE = new Animation<>(0.1f,
            SpriteSheet.ORIGINAL_BOMBERMAN.at(7, 5),
            SpriteSheet.ORIGINAL_BOMBERMAN.at(7, 10),
            SpriteSheet.ORIGINAL_BOMBERMAN.at(7, 5),
            SpriteSheet.ORIGINAL_BOMBERMAN.at(7, 10)
    );

    // DOWN BLAST
    public static final Animation<TextureRegion> BLAST_DOWN_INITIAL_PIECE = new Animation<>(0.1f,
            SpriteSheet.ORIGINAL_BOMBERMAN.at(7, 4),
            SpriteSheet.ORIGINAL_BOMBERMAN.at(7, 9),
            SpriteSheet.ORIGINAL_BOMBERMAN.at(12, 4),
            SpriteSheet.ORIGINAL_BOMBERMAN.at(12, 9)
    );

    public static final Animation<TextureRegion> BLAST_DOWN_END_PIECE = new Animation<>(0.1f,
            SpriteSheet.ORIGINAL_BOMBERMAN.at(7, 5),
            SpriteSheet.ORIGINAL_BOMBERMAN.at(7, 10),
            SpriteSheet.ORIGINAL_BOMBERMAN.at(7, 5),
            SpriteSheet.ORIGINAL_BOMBERMAN.at(7, 10)
    );






}
