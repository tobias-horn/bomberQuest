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

    // Variables to save position before destroying the body
    private float savedX, savedY;

    // Reference to the game map for applying effects
    private final GameMap gameMap;

    // Sound effect for the explosion
    private static final Sound explosionSound = Gdx.audio.newSound(Gdx.files.internal("assets/audio/explosionSound.mp3"));

    // --------------------------------------------
    // NEW FIELDS FOR EXPLOSION ANIMATION LIFESPAN
    // --------------------------------------------
    /** How long we keep this Bomb object around (for center animation) after it explodes. */
    private float explosionTimer = Animations.BOMB_CENTER_EXPLOSION.getAnimationDuration();

    /** Whether we have already spawned the ExplosionTile objects for the plus-shape. */
    private boolean explosionInitialized = false;

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
        fixture.setSensor(true); // Bomb is just a sensor.

        shape.dispose();
        body.setUserData(this);
    }

    public void startTimer() {
        if (hasExploded) return;
        this.remainingTime = 3f; // Bomb explodes after 3 seconds
    }

    public void explode() {
        if (hasExploded) return;
        hasExploded = true;
        explosionSound.play();

        // Save the position before destroying the body
        savedX = getX();
        savedY = getY();

        affectArea();

        if (body != null) {
            body.getWorld().destroyBody(body);
            body = null; // Nullify the body
        }

        explosionTimer = 0.4f; // Keep the bomb around for the animation
    }

    public void update(float deltaTime) {
        // 1) Countdown to explosion
        if (remainingTime > 0) {
            remainingTime -= deltaTime;
            if (remainingTime <= 0) {
                explode();
            }
        }

        // 2) If bomb has exploded, handle the explosion-lifespan timer
        if (hasExploded) {
            // Spawn the plus-shape explosion tiles only once
            if (!explosionInitialized) {
                spawnExplosionTiles();
                explosionInitialized = true;
            }

            // Decrement the bomb's own animation timer
            if (explosionTimer > 0) {
                explosionTimer -= deltaTime;
            }
        }

        elapsedTime += deltaTime;
    }

    // The main explosion logic
    private void affectArea() {
        int bombTileX = (int) Math.floor(getX());
        int bombTileY = (int) Math.floor(getY());

        // "self" tile
        applyExplosionEffects(bombTileX, bombTileY);

        // Expand outward in all four directions
        checkDirection(bombTileX, bombTileY, 1, 0);  // Right
        checkDirection(bombTileX, bombTileY, -1, 0); // Left
        checkDirection(bombTileX, bombTileY, 0, 1);  // Up
        checkDirection(bombTileX, bombTileY, 0, -1); // Down
    }

    private void checkDirection(int startX, int startY, int dx, int dy) {
        for (int i = 1; i <= radius; i++) {
            int checkX = startX + i * dx;
            int checkY = startY + i * dy;

            // If we hit a wall, stop
            boolean shouldStop = applyExplosionEffects(checkX, checkY);
            if (shouldStop) break;
        }
    }

    private boolean applyExplosionEffects(int tileX, int tileY) {
        if (destroyWalls(tileX, tileY)) return true;

        damagePlayerIfPresent(tileX, tileY);
        damageEnemiesIfPresent(tileX, tileY);

        return false;
    }

    private boolean destroyWalls(int tileX, int tileY) {
        GameObject obj = gameMap.getObjectAt(tileX, tileY);

        if (obj instanceof IndestructibleWall) {
            return true; // stop
        } else if (obj instanceof DestructibleWall destructibleWall) {
            destructibleWall.startFading(); // Start fade animation

            if (destructibleWall.getPowerUpUnderneath() != null) {
                // spawn powerup
                gameMap.getMap().put(new Vector2(tileX, tileY),
                        new PowerUp(gameMap.getWorld(), tileX, tileY, destructibleWall.getPowerUpUnderneath()));
            }
            return false; // keep expanding, since destructible walls do not block
        }

        return false;
    }

    private void damagePlayerIfPresent(int tileX, int tileY) {
        int px = (int) Math.floor(gameMap.getPlayer().getX());
        int py = (int) Math.floor(gameMap.getPlayer().getY());
        if (px == tileX && py == tileY) {
            gameMap.getGame().goToGameOver();
        }
    }

    private void damageEnemiesIfPresent(int tileX, int tileY) {
        Iterator<Enemy> it = gameMap.getEnemies().iterator();
        while (it.hasNext()) {
            Enemy e = it.next();
            if (e.getBody() == null) continue;
            int ex = (int) Math.floor(e.getX());
            int ey = (int) Math.floor(e.getY());
            if (ex == tileX && ey == tileY) {
                e.getBody().getWorld().destroyBody(e.getBody());
                e.setBody(null);
                it.remove();
            }
        }
    }

    // ----------------------------------------------------------------------
    // Create ExplosionTile objects for the animation
    // ----------------------------------------------------------------------
    private void spawnExplosionTiles() {
        int bombTileX = (int) Math.floor(getX());
        int bombTileY = (int) Math.floor(getY());

        // 1) Add the center tile
        ExplosionTile center = new ExplosionTile(gameMap, bombTileX, bombTileY, ExplosionTile.Type.CENTER);
        gameMap.addExplosionTile(center);

        // 2) For each direction, create the appropriate tiles
        spawnDirectionalTiles(bombTileX, bombTileY,  1, 0); // Right
        spawnDirectionalTiles(bombTileX, bombTileY, -1, 0); // Left
        spawnDirectionalTiles(bombTileX, bombTileY, 0,  1); // Up
        spawnDirectionalTiles(bombTileX, bombTileY, 0, -1); // Down
    }

    private void spawnDirectionalTiles(int startX, int startY, int dx, int dy) {
        for (int i = 1; i <= radius; i++) {
            int tx = startX + i * dx;
            int ty = startY + i * dy;

            // If we hit an IndestructibleWall, no tile is drawn
            // If we destroy a destructible wall, we STILL want the explosion to pass
            GameObject obj = gameMap.getObjectAt(tx, ty);
            if (obj instanceof IndestructibleWall) {
                break; // stop drawing further tiles
            }

            boolean isEnd = (i == radius);
            ExplosionTile.Type type = pickTileType(dx, dy, isEnd);
            ExplosionTile tile = new ExplosionTile(gameMap, tx, ty, type);
            gameMap.addExplosionTile(tile);

            if (obj instanceof DestructibleWall) {
                // We still break from drawing beyond this tile if we want
                // the explosion to pass destructible walls, do not break
                // but it’s your choice.
            }
        }
    }

    // Helper to choose correct explosion tile type (middle vs end, horizontal vs vertical)
    private ExplosionTile.Type pickTileType(int dx, int dy, boolean isEnd) {
        // Check horizontal directions
        if (dx > 0) {
            return isEnd ? ExplosionTile.Type.RIGHT_END : ExplosionTile.Type.RIGHT_MIDDLE;
        }
        if (dx < 0) {
            return isEnd ? ExplosionTile.Type.LEFT_END : ExplosionTile.Type.LEFT_MIDDLE;
        }
        // Check vertical directions
        if (dy > 0) {
            return isEnd ? ExplosionTile.Type.UP_END : ExplosionTile.Type.UP_MIDDLE;
        }
        if (dy < 0) {
            return isEnd ? ExplosionTile.Type.DOWN_END : ExplosionTile.Type.DOWN_MIDDLE;
        }
        // Default case (center of explosion)
        return ExplosionTile.Type.CENTER;
    }

    @Override
    public TextureRegion getCurrentAppearance() {
        // If not exploded, show bomb animation
        if (!hasExploded) {
            return Animations.BOMB_GOING_OFF.getKeyFrame(this.elapsedTime, true);
        }
        // If exploded, optionally show a "center explosion" animation
        return Animations.BOMB_CENTER_EXPLOSION.getKeyFrame(this.elapsedTime, false);
    }

    public float getRadius() { return radius; }
    public void setRadius(float radius) { this.radius = radius; }
    public boolean isHasExploded() { return hasExploded; }

    // Use this in GameMap.bombs removal logic
    public boolean isExplosionFinished() {
        return (hasExploded && explosionTimer <= 0);
    }

    @Override
    public float getX() {
        return body != null ? body.getPosition().x : savedX;
    }

    @Override
    public float getY() {
        return body != null ? body.getPosition().y : savedY;
    }
}