package de.tum.cit.ase.bomberquest.objects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import de.tum.cit.ase.bomberquest.map.GameMap;
import de.tum.cit.ase.bomberquest.textures.Animations;
import de.tum.cit.ase.bomberquest.textures.Drawable;

/**
 * Represents a single tile of the bomb explosion animation.
 * Each tile is short-lived and will remove itself after a set duration.
 */
public class ExplosionTile implements Drawable {

    /**
     * Defines the type of explosion tile, determining its appearance.
     */
    public enum Type {
        CENTER,
        LEFT_END,
        RIGHT_END,
        UP_END,
        DOWN_END,
        UP_MIDDLE,
        DOWN_MIDDLE,
        RIGHT_MIDDLE,
        LEFT_MIDDLE
    }

    private final GameMap gameMap;
    private final int tileX;
    private final int tileY;
    private final Type type;

    private float elapsedTime = 0f;
    private float duration = Animations.BOMB_CENTER_EXPLOSION.getAnimationDuration();

    /**
     * Constructs an ExplosionTile with the specified parameters.
     *
     * @param gameMap the game map where the explosion occurs
     * @param tileX   the x-coordinate of the tile
     * @param tileY   the y-coordinate of the tile
     * @param type    the type of explosion tile
     */
    public ExplosionTile(GameMap gameMap, int tileX, int tileY, Type type) {
        this.gameMap = gameMap;
        this.tileX = tileX;
        this.tileY = tileY;
        this.type = type;
    }

    /**
     * Updates the state of the explosion tile based on the elapsed time.
     *
     * @param deltaTime the time elapsed since the last update
     */
    public void update(float deltaTime) {
        elapsedTime += deltaTime;
        duration -= deltaTime;
    }


    public boolean isFinished() {
        return (duration <= 0);
    }

    @Override
    public float getX() {
        return tileX + 0.5f;
    }

    @Override
    public float getY() {
        return tileY + 0.5f;
    }

    /**
     * Retrieves the current texture representing the explosion tile based on its type.
     *
     * @return the texture region for the current appearance of the explosion tile
     */
    @Override
    public TextureRegion getCurrentAppearance() {
        switch (type) {
            case CENTER:
                return Animations.BOMB_CENTER_EXPLOSION.getKeyFrame(elapsedTime, false);
            case LEFT_END:
                return Animations.BLAST_LEFT_END_PIECE.getKeyFrame(elapsedTime, false);
            case RIGHT_END:
                return Animations.BLAST_RIGHT_END_PIECE.getKeyFrame(elapsedTime, false);
            case UP_END:
                return Animations.BLAST_UP_END_PIECE.getKeyFrame(elapsedTime, false);
            case DOWN_END:
                return Animations.BLAST_DOWN_END_PIECE.getKeyFrame(elapsedTime, false);
            case RIGHT_MIDDLE:
                return Animations.BLAST_RIGHT_INITIAL_PIECE.getKeyFrame(elapsedTime, false);
            case LEFT_MIDDLE:
                return Animations.BLAST_LEFT_INITIAL_PIECE.getKeyFrame(elapsedTime, false);
            case UP_MIDDLE:
                return Animations.BLAST_UP_INITIAL_PIECE.getKeyFrame(elapsedTime, false);
            case DOWN_MIDDLE:
                return Animations.BLAST_DOWN_INITIAL_PIECE.getKeyFrame(elapsedTime, false);
            default:
                return Animations.BOMB_CENTER_EXPLOSION.getKeyFrame(elapsedTime, false);
        }
    }
}
