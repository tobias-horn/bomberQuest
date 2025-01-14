package de.tum.cit.ase.bomberquest.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import de.tum.cit.ase.bomberquest.map.GameMap;
import de.tum.cit.ase.bomberquest.textures.Animations;
import de.tum.cit.ase.bomberquest.textures.Drawable;

import java.util.Iterator;

/**
 * The Bomb class represents a bomb in the game.
 * It handles the bomb's timer, explosion effects, and interactions with the game map.
 */
public class Bomb extends GameObject implements Drawable {

    // Time left before the bomb explodes
    private float remainingTime;

    // The radius of the explosion (how far it affects tiles)
    private float radius;

    // Whether the bomb has already exploded
    private boolean hasExploded;

    // Time elapsed since the bomb was placed (used for animations)
    private float elapsedTime;

    // Reference to the game map for applying effects
    private final GameMap gameMap;

    // Sound effect for the explosion
    private static final Sound explosionSound = Gdx.audio.newSound(Gdx.files.internal("assets/audio/explosionSound.mp3"));


    /**
     * Creates a new bomb object.
     * @param world The Box2D world the bomb exists in.
     * @param tileX The x-coordinate (in tiles) where the bomb is placed.
     * @param tileY The y-coordinate (in tiles) where the bomb is placed.
     * @param radius The radius of the explosion in tiles.
     * @param gameMap The game map, used for affecting objects.
     */
    public Bomb(World world, float tileX, float tileY, float radius, GameMap gameMap) {
        super(world, tileX, tileY); // Calls the GameObject constructor to create the bomb's hitbox.
        this.radius = radius;
        this.gameMap = gameMap;
        this.hasExploded = false; // The bomb hasn't exploded yet.
        this.remainingTime = 0f; // Timer starts at 0 (not yet counting down).
        this.elapsedTime = 0; // Used for animation purposes.
    }

    /**
     * Sets up the bomb's hitbox for collision detection.
     * This creates a static, sensor-type Box2D body.
     */
    @Override
    protected void createHitbox(World world, float tileX, float tileY) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody; // The bomb doesn't move.
        bodyDef.position.set(tileX + 0.5f, tileY + 0.5f); // Center the bomb in its tile.

        body = world.createBody(bodyDef);

        // Define a square hitbox that matches the size of a tile.
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.5f, 0.5f);

        Fixture fixture = body.createFixture(shape, 1f);
        fixture.setSensor(true); // Sensors detect collisions without affecting physics.

        shape.dispose(); // Clean up to prevent memory leaks.
        body.setUserData(this); // Link the body back to this Bomb object.
    }

    /**
     * Starts the countdown timer for the bomb.
     * By default, the bomb explodes after 3 seconds.
     */
    public void startTimer() {
        if (hasExploded) return; // If already exploded, do nothing.
        this.remainingTime = 3f; // Set the timer to 3 seconds.
    }

    /**
     * Causes the bomb to explode immediately.
     * Plays the explosion sound, applies area effects, and removes the bomb's hitbox.
     */
    public void explode() {
        if (hasExploded) return; // Prevent multiple explosions.
        hasExploded = true; // Mark the bomb as exploded.
        explosionSound.play(); // Play the explosion sound.

        affectArea(); // Apply effects to the surrounding area.

        // Remove the bomb's hitbox from the physics world.
        if (body != null) body.getWorld().destroyBody(body);
    }

    /**
     * Updates the bomb's state (called every frame).
     * @param deltaTime Time elapsed since the last frame.
     */
    public void update(float deltaTime) {
        // Decrease the timer if it's active.
        if (remainingTime > 0) {
            remainingTime -= deltaTime; // Countdown.
            if (remainingTime <= 0) explode(); // Explode when the timer reaches 0.
        }

        // Update elapsed time for animation purposes.
        elapsedTime += deltaTime;
    }

    /**
     * Handles the effects of the bomb's explosion.
     * The explosion affects the bomb's tile and expands outward in all directions.
     */
    private void affectArea() {
        int bombTileX = (int) Math.floor(getX());
        int bombTileY = (int) Math.floor(getY());

        // Apply effects to the bomb's own tile.
        applyExplosionEffects(bombTileX, bombTileY);

        // Expand outward in all four directions.
        checkDirection(bombTileX, bombTileY, 1,  0); // Right.
        checkDirection(bombTileX, bombTileY, -1, 0); // Left.
        checkDirection(bombTileX, bombTileY, 0,  1); // Up.
        checkDirection(bombTileX, bombTileY, 0, -1); // Down.
    }

    /**
     * Handles the explosion in a specific direction.
     * Stops if a wall blocks the explosion.
     */
    private void checkDirection(int startX, int startY, int dx, int dy) {
        for (int i = 1; i <= radius; i++) {
            int checkX = startX + i * dx;
            int checkY = startY + i * dy;

            // Stop expanding if a wall is hit.
            if (applyExplosionEffects(checkX, checkY)) break;
        }
    }

    /**
     * Applies explosion effects to a specific tile.
     * @return True if the explosion should stop expanding (e.g., due to a wall).
     */
    private boolean applyExplosionEffects(int tileX, int tileY) {
        if (destroyWalls(tileX, tileY)) return true; // Stop if a wall blocks the explosion.

        damagePlayerIfPresent(tileX, tileY); // Damage the player if they're on this tile.
        damageEnemiesIfPresent(tileX, tileY); // Damage enemies on this tile.

        return false; // Keep expanding if nothing blocks the explosion.
    }

    /**
     * Checks for walls on the tile and handles their destruction if destructible.
     * @return True if the explosion stops due to a wall.
     */
    private boolean destroyWalls(int tileX, int tileY) {
        GameObject obj = gameMap.getObjectAt(tileX, tileY);

        if (obj instanceof IndestructibleWall) {
            return true; // Indestructible walls block the explosion.
        } else if (obj instanceof DestructibleWall) {
            gameMap.removeObjectAt(tileX, tileY); // Destroy destructible walls.
            return true; // Explosion stops here.
        }

        return false; // No wall => keep expanding.
    }

    /**
     * Damages the player if they're within the explosion radius.
     * Ends the game if the player is hit.
     */
    private void damagePlayerIfPresent(int tileX, int tileY) {
        int playerTileX = (int) Math.floor(gameMap.getPlayer().getX());
        int playerTileY = (int) Math.floor(gameMap.getPlayer().getY());

        if (playerTileX == tileX && playerTileY == tileY) {
            gameMap.getGame().goToGameOver(); // End the game if the player is on the tile.
        }
    }

    /**
     * Damages enemies within the explosion radius.
     * Removes enemies from the game if hit.
     */
    private void damageEnemiesIfPresent(int tileX, int tileY) {
        Iterator<Enemy> it = gameMap.getEnemies().iterator();
        while (it.hasNext()) {
            Enemy e = it.next();

            if (e.getBody() == null) continue; // Skip already removed enemies.

            int ex = (int) Math.floor(e.getX());
            int ey = (int) Math.floor(e.getY());

            if (ex == tileX && ey == tileY) {
                e.getBody().getWorld().destroyBody(e.getBody());
                e.setBody(null); // Remove the enemy's body.
                it.remove(); // Remove the enemy from the list.
            }
        }
    }

    @Override
    public TextureRegion getCurrentAppearance() {
        return Animations.BOMB_GOING_OFF.getKeyFrame(this.elapsedTime, true);
    }

    // Getters and setters for the bomb's radius and explosion state.
    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public boolean isHasExploded() {
        return hasExploded;
    }
}