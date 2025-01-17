package de.tum.cit.ase.bomberquest.textures;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

/**
 * Contains all animation constants used in the game.
 * It is good practice to keep all textures and animations in constants to avoid loading them multiple times.
 * These can be referenced anywhere they are needed.
 */
public class Animations {
    
    /**
     * The animation for the character walking down.
     */

    // CHARACTER WALKING DOWN
    public static final Animation<TextureRegion> CHARACTER_WALK_DOWN = new Animation<>(0.1f,
            SpriteSheet.PLAYER.at(2, 1, 1, 2),
            SpriteSheet.PLAYER.at(2, 2, 1, 2),
            SpriteSheet.PLAYER.at(2, 3, 1, 2),
            SpriteSheet.PLAYER.at(2, 4, 1, 2),
            SpriteSheet.PLAYER.at(2, 5, 1, 2),
            SpriteSheet.PLAYER.at(2, 6, 1, 2),
            SpriteSheet.PLAYER.at(2, 7, 1, 2),
            SpriteSheet.PLAYER.at(2, 8, 1, 2)
    );

    // CHARACTER WALKING RIGHT
    public static final Animation<TextureRegion> CHARACTER_WALK_RIGHT = new Animation<>(0.1f,
            SpriteSheet.PLAYER.at(5, 1, 1, 2),
            SpriteSheet.PLAYER.at(5, 2, 1, 2),
            SpriteSheet.PLAYER.at(5, 3, 1, 2),
            SpriteSheet.PLAYER.at(5, 4, 1, 2)
    );

    // CHARACTER WALKING UP
    public static final Animation<TextureRegion> CHARACTER_WALK_UP = new Animation<>(0.1f,
            SpriteSheet.PLAYER.at(8, 1, 1, 2),
            SpriteSheet.PLAYER.at(8, 2, 1, 2),
            SpriteSheet.PLAYER.at(8, 3, 1, 2),
            SpriteSheet.PLAYER.at(8, 4, 1, 2)
    );

    // CHARACTER WALKING LEFT
    public static final Animation<TextureRegion> CHARACTER_WALK_LEFT;

    static {
        Array<TextureRegion> walkLeftFrames = new Array<>();
        // Iterate through the frames of the "walking right" animation and flip them
        for (TextureRegion frame : new TextureRegion[]{
                SpriteSheet.PLAYER.at(5, 1, 1, 2),
                SpriteSheet.PLAYER.at(5, 2, 1, 2),
                SpriteSheet.PLAYER.at(5, 3, 1, 2),
                SpriteSheet.PLAYER.at(5, 4, 1, 2)
        }) {
            TextureRegion flippedFrame = new TextureRegion(frame);
            flippedFrame.flip(true, false);
            walkLeftFrames.add(flippedFrame);
        }
        CHARACTER_WALK_LEFT = new Animation<>(0.1f, walkLeftFrames);
    }


    public static final Animation<TextureRegion> CHARACTER_IDLE = new Animation<>(0.1f,
            SpriteSheet.PLAYER.at(2, 4, 1, 2)
    );




    // ENEMY WALKING UP
    public static final Animation<TextureRegion> ENEMY_WALK_UP = new Animation<>(0.1f,
            SpriteSheet.ENEMY.at(4, 10),
            SpriteSheet.ENEMY.at(4, 11),
            SpriteSheet.ENEMY.at(4, 12)
    );

    // ENEMY WALKING DOWN
    public static final Animation<TextureRegion> ENEMY_WALK_DOWN = new Animation<>(0.1f,
            SpriteSheet.ENEMY.at(1, 10),
            SpriteSheet.ENEMY.at(1, 11),
            SpriteSheet.ENEMY.at(1, 12)
    );

    // ENEMY WALKING RIGHT
    public static final Animation<TextureRegion> ENEMY_WALK_RIGHT = new Animation<>(0.1f,
            SpriteSheet.ENEMY.at(3, 10),
            SpriteSheet.ENEMY.at(3, 11),
            SpriteSheet.ENEMY.at(3, 12)
    );

    // ENEMY WALKING LEFT
    public static final Animation<TextureRegion> ENEMY_WALK_LEFT = new Animation<>(0.1f,
            SpriteSheet.ENEMY.at(2, 10),
            SpriteSheet.ENEMY.at(2, 11),
            SpriteSheet.ENEMY.at(2, 12)
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
