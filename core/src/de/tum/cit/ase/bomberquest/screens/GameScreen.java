package de.tum.cit.ase.bomberquest.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ScreenUtils;
import de.tum.cit.ase.bomberquest.BomberQuestGame;
import de.tum.cit.ase.bomberquest.ScreenStates.PauseScreen;
import de.tum.cit.ase.bomberquest.ScreenStates.ScreenState;
import de.tum.cit.ase.bomberquest.bonusFeatures.Score;
import de.tum.cit.ase.bomberquest.map.GameMap;
import de.tum.cit.ase.bomberquest.objects.*;
import de.tum.cit.ase.bomberquest.textures.Drawable;
import de.tum.cit.ase.bomberquest.textures.Textures;
import de.tum.cit.ase.bomberquest.bonusFeatures.ui.KeyBindings;

/**
 * Represents the main game screen, where the gameplay occurs.
 * Manages rendering, game logic updates, user input, and transitions between game states.
 */
public class GameScreen implements Screen {

    public static final int TILE_SIZE_PX = 32; // Size of a tile in pixels
    public static final int SCALE = 2; // Scale factor for rendering

    // References to core game components
    private final BomberQuestGame game;
    private final SpriteBatch spriteBatch;
    private final GameMap map;
    private final Hud hud;
    private final OrthographicCamera mapCamera;
    private PauseScreen pauseScreen;

    private boolean paused = false; // Tracks whether the game is paused
    private float remainingTime; // Remaining time for the level
    private final float initialTime = 5 * 60f; // Initial time for the level (5 minutes)
    private float blinkAccumulator = 0f; // Accumulates time for blink effects
    private boolean blinkToggle = false; // Toggles the blink state
    private boolean isGameOver = false; // Tracks whether the game is over

    private Score score;

    /**
     * Constructs the GameScreen with the provided game instance.
     *
     * @param game The main game instance.
     */
    public GameScreen(BomberQuestGame game, Score score) {
        this.game = game;
        this.spriteBatch = game.getSpriteBatch();
        this.map = game.getMap();
        this.hud = game.getHud();
        this.mapCamera = new OrthographicCamera();
        this.mapCamera.setToOrtho(false);
        this.pauseScreen = new PauseScreen(game, game.getSkin().getFont("font"));
        this.remainingTime = initialTime;
        this.score = score;
    }

    /**
     * Renders the game screen, updating game logic and handling user input.
     *
     * @param deltaTime Time elapsed since the last frame.
     */
    @Override
    public void render(float deltaTime) {
        if (!paused) {


            map.tick(deltaTime);

            float moveSpeed = 2f; // Player's speed
            float vx = 0; // Horizontal velocity
            float vy = 0; // Vertical velocity

            // Player movement
            if (Gdx.input.isKeyPressed(KeyBindings.getKey(KeyBindings.MOVE_UP))) {
                vy += moveSpeed;
            }
            if (Gdx.input.isKeyPressed(KeyBindings.getKey(KeyBindings.MOVE_DOWN))) {
                vy -= moveSpeed;
            }
            if (Gdx.input.isKeyPressed(KeyBindings.getKey(KeyBindings.MOVE_LEFT))) {
                vx -= moveSpeed;
            }
            if (Gdx.input.isKeyPressed(KeyBindings.getKey(KeyBindings.MOVE_RIGHT))) {
                vx += moveSpeed;
            }

            // Normalize the velocity vector if moving diagonally
            float magnitude = (float) Math.sqrt(vx * vx + vy * vy);
            if (magnitude > 0) {
                vx = (vx / magnitude) * moveSpeed;
                vy = (vy / magnitude) * moveSpeed;
            }

            map.getPlayer().updateDirection(vx, vy);
            map.getPlayer().update(deltaTime);

            if (Gdx.input.isKeyJustPressed(KeyBindings.getKey(KeyBindings.PLACE_BOMB))) {
                float px = map.getPlayer().getX();
                float py = map.getPlayer().getY();

                int tileX = (int) Math.floor(px);
                int tileY = (int) Math.floor(py);

                Bomb bomb = new Bomb(map.getWorld(), tileX, tileY, 1, map, score);
                bomb.setRadius(map.getBlastRadius());
                bomb.startTimer();
                map.addBomb(bomb);
            }

            map.getPlayer().updateDirection(vx, vy);


            remainingTime -= deltaTime;
            if (remainingTime < 0) {
                remainingTime = 0;
            }
        }

        if (Gdx.input.isKeyJustPressed(KeyBindings.getKey(KeyBindings.PAUSE_GAME))) {
            setPaused(!paused);
        }

        ScreenUtils.clear(Color.BLACK);

        updateCamera();
        renderBackground();
        renderMap();

        int minutes = (int) (remainingTime / 60);
        int seconds = (int) (remainingTime % 60);
        String timerText = String.format("%02d:%02d", minutes, seconds);

        Hud.PanelState state = Hud.PanelState.BLACK;
        if (remainingTime < 10) {
            blinkAccumulator += deltaTime;
            if (blinkAccumulator >= 0.5f) {
                blinkToggle = !blinkToggle;
                blinkAccumulator = 0f;
            }
            if(!map.getEnemies().isEmpty()) {
                state = blinkToggle ? Hud.PanelState.RED : Hud.PanelState.BLACK;
            } else {
                state = blinkToggle ? Hud.PanelState.BLUE : Hud.PanelState.BLACK;
            }

        } else if (remainingTime < 60) {
            state = (map.getEnemies().isEmpty()) ? Hud.PanelState.BLUE : Hud.PanelState.RED;
            blinkAccumulator = 0f;
            blinkToggle = false;
        } else {
            state = (map.getEnemies().isEmpty()) ? Hud.PanelState.BLUE : Hud.PanelState.BLACK;
            blinkAccumulator = 0f;
            blinkToggle = false;
        }

        hud.setPanelState(state);
        hud.setCounts(map.getConcurrentBombCount(), map.getBlastRadius(), map.getRemainingEnemiesCount());

        // Update HUD with current time (in seconds) and current score
        hud.setTimerInSeconds(minutes * 60 + seconds);
        hud.setScore(map.getScore().getScore());

        hud.render(timerText);

        if(remainingTime == 0){
            game.goToGameOver();
        }

        if (paused) {
            pauseScreen.render();
        }
    }

    /**
     * Sets the paused state of the game, pausing or resuming game logic and music accordingly.
     *
     * @param shouldPause True to pause the game, false to resume.
     */
    public void setPaused(boolean shouldPause) {
        this.paused = shouldPause;
        // Update the game's screen state
        if (shouldPause) {
            game.setCurrentScreenState(ScreenState.PAUSE);
        } else {
            game.setCurrentScreenState(ScreenState.GAME);
        }

        // Pause or resume music based on the paused state
        if (paused) {
            if (game.getCurrentMusicTrack() != null) {
                game.getCurrentMusicTrack().pause();
            }
            Gdx.app.log("Pause", "Pausing and setting input processor to PauseScreen stage");
            Gdx.input.setInputProcessor(pauseScreen.getStage());
        } else {
            if (game.getCurrentMusicTrack() != null) {
                game.getCurrentMusicTrack().play();
            }
            Gdx.app.log("Pause", "Unpausing; removing PauseScreen stage input processor");
            Gdx.input.setInputProcessor(null);
        }
    }

    /**
     * Resumes the game from a paused state.
     */
    public void resumeGame() {
        setPaused(false);
    }

    /**
     * Updates the camera position to follow the player while keeping the camera within map bounds.
     */
    private void updateCamera() {
        float halfW = mapCamera.viewportWidth * 0.5f;
        float halfH = mapCamera.viewportHeight * 0.5f;
        float cameraLeft = mapCamera.position.x - halfW;
        float cameraRight = mapCamera.position.x + halfW;
        float cameraBottom = mapCamera.position.y - halfH;
        float cameraTop = mapCamera.position.y + halfH;

        float marginX = 0.2f * mapCamera.viewportWidth;
        float marginY = 0.2f * mapCamera.viewportHeight;

        float playerX = map.getPlayer().getX() * TILE_SIZE_PX * SCALE;
        float playerY = map.getPlayer().getY() * TILE_SIZE_PX * SCALE;

        if (playerX < cameraLeft + marginX) {
            mapCamera.position.x = playerX + (halfW - marginX);
        } else if (playerX > cameraRight - marginX) {
            mapCamera.position.x = playerX - (halfW - marginX);
        }

        if (playerY < cameraBottom + marginY) {
            mapCamera.position.y = playerY + (halfH - marginY);
        } else if (playerY > cameraTop - marginY) {
            mapCamera.position.y = playerY - (halfH - marginY);
        }

        float mapWidthInPx = map.getWidth() * TILE_SIZE_PX * SCALE;
        float mapHeightInPx = map.getHeight() * TILE_SIZE_PX * SCALE;

        float minCameraX = halfW;
        float maxCameraX = mapWidthInPx - halfW;
        float minCameraY = halfH;
        float maxCameraY = mapHeightInPx - halfH;

        if (mapCamera.position.x < minCameraX) {
            mapCamera.position.x = minCameraX;
        }
        if (mapCamera.position.x > maxCameraX) {
            mapCamera.position.x = maxCameraX;
        }
        if (mapCamera.position.y < minCameraY) {
            mapCamera.position.y = minCameraY;
        }
        if (mapCamera.position.y > maxCameraY) {
            mapCamera.position.y = maxCameraY;
        }

        mapCamera.update();
    }

    /**
     * Renders all drawable objects on the map, including the player, enemies, bombs, and explosions.
     */
    private void renderMap() {
        spriteBatch.setProjectionMatrix(mapCamera.combined);
        spriteBatch.begin();

        for (GameObject obj : map.getAllObjects()) {
            if (obj instanceof Drawable drawableObj && obj.getBody() != null) {
                draw(spriteBatch, drawableObj);
            }
            if (obj instanceof Exit) {

            }
        }

        for (Enemy enemy : map.getEnemies()) {
            draw(spriteBatch, enemy);
        }

        for (Bomb bomb : map.getBombs()) {
            draw(spriteBatch, bomb);
        }

        // DRAW EXPLOSION TILES (NEW)
        for (ExplosionTile explosionTile : map.getExplosionTiles()) {
            draw(spriteBatch, explosionTile);
        }

        if (map.getPlayer() != null) {
            Drawable player = map.getPlayer();
            TextureRegion texture = player.getCurrentAppearance();

            float playerScaleFactor = 0.8f; // Scale the player size (texture) individually

            float spriteWidthInWorldUnits = ((float) texture.getRegionWidth() / TILE_SIZE_PX) * playerScaleFactor;
            float spriteHeightInWorldUnits = ((float) texture.getRegionHeight() / TILE_SIZE_PX) * playerScaleFactor;

            float x = (player.getX() - (spriteWidthInWorldUnits / 2)) * TILE_SIZE_PX * SCALE;
            float y = (player.getY() - (spriteHeightInWorldUnits / 2)) * TILE_SIZE_PX * SCALE;

            float width = texture.getRegionWidth() * SCALE * playerScaleFactor;
            float height = texture.getRegionHeight() * SCALE * playerScaleFactor;

            spriteBatch.draw(texture, x, y, width, height);
        }

        spriteBatch.end();
    }

    /**
     * Renders the background tiles based on the camera's current viewport.
     */
    private void renderBackground() {
        spriteBatch.setProjectionMatrix(mapCamera.combined);
        spriteBatch.begin();

        TextureRegion backgroundTile = Textures.BACKGROUND;
        float tileSizeInWorldUnits = TILE_SIZE_PX * SCALE;

        float startX = mapCamera.position.x - (mapCamera.viewportWidth / 2);
        float startY = mapCamera.position.y - (mapCamera.viewportHeight / 2);

        int startTileX = (int) (startX / tileSizeInWorldUnits);
        int startTileY = (int) (startY / tileSizeInWorldUnits);
        int endTileX = (int) ((startX + mapCamera.viewportWidth) / tileSizeInWorldUnits) + 1;
        int endTileY = (int) ((startY + mapCamera.viewportHeight) / tileSizeInWorldUnits) + 1;

        for (int x = startTileX; x <= endTileX; x++) {
            for (int y = startTileY; y <= endTileY; y++) {
                float drawX = x * tileSizeInWorldUnits;
                float drawY = y * tileSizeInWorldUnits;
                spriteBatch.draw(backgroundTile, drawX, drawY, tileSizeInWorldUnits, tileSizeInWorldUnits);
            }
        }
        spriteBatch.end();
    }

    /**
     * Draws a drawable object using the provided SpriteBatch.
     *
     * @param spriteBatch The SpriteBatch used for rendering.
     * @param drawable    The drawable object to render.
     */
    private static void draw(SpriteBatch spriteBatch, Drawable drawable) {
        TextureRegion texture = drawable.getCurrentAppearance();
        // Apply scaling for specific objects
        float scale = 1.0f; // Default scale
        if (drawable instanceof ExplosionTile) {
            scale = 2.0f; // Scale ExplosionTile by 2
        } else if (drawable instanceof Enemy) {
            scale = 1.5f; // Scale Enemy by 1.5
        }

        float spriteWidthInWorldUnits = ((float) texture.getRegionWidth() / TILE_SIZE_PX) * scale;
        float spriteHeightInWorldUnits = ((float) texture.getRegionHeight() / TILE_SIZE_PX) * scale;

        float x = (drawable.getX() - (spriteWidthInWorldUnits / 2)) * TILE_SIZE_PX * SCALE;
        float y = (drawable.getY() - (spriteHeightInWorldUnits / 2)) * TILE_SIZE_PX * SCALE;

        float width = texture.getRegionWidth() * SCALE * scale;
        float height = texture.getRegionHeight() * SCALE * scale;

        spriteBatch.draw(texture, x, y, width, height);
    }

    /**
     * Handles resizing of the screen by updating the camera viewport and HUD.
     *
     * @param width  The new width of the screen.
     * @param height The new height of the screen.
     */
    @Override
    public void resize(int width, int height) {
        mapCamera.setToOrtho(false, width, height);
        hud.resize(width, height);
        pauseScreen.resize(width, height);
    }

    /**
     * Disposes of resources used by the game screen, including the pause screen.
     */
    @Override
    public void dispose() {
        pauseScreen.dispose();
    }

    @Override
    public void pause() {
        // Not implemented
    }

    @Override
    public void resume() {
        // Not implemented
    }

    @Override
    public void show() {
        // Not implemented
    }

    @Override
    public void hide() {
        // Not implemented
    }

    /**
     * Sets the game over state.
     *
     * @param b True if the game is over, false otherwise.
     */
    public void setGameOver(boolean b) {
        // Implementation can be added as needed
    }
}
