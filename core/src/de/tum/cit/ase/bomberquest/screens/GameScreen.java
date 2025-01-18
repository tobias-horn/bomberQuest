package de.tum.cit.ase.bomberquest.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ScreenUtils;
import de.tum.cit.ase.bomberquest.BomberQuestGame;
import de.tum.cit.ase.bomberquest.map.GameMap;
import de.tum.cit.ase.bomberquest.objects.Bomb;
import de.tum.cit.ase.bomberquest.objects.Enemy;
import de.tum.cit.ase.bomberquest.objects.GameObject;
import de.tum.cit.ase.bomberquest.textures.Drawable;
import de.tum.cit.ase.bomberquest.textures.Textures;
import de.tum.cit.ase.bomberquest.ui.KeyBindings;

public class GameScreen implements Screen {

    public static final int TILE_SIZE_PX = 32;
    public static final int SCALE = 2;

    private final BomberQuestGame game;
    private final SpriteBatch spriteBatch;
    private final GameMap map;
    private final Hud hud;
    private final OrthographicCamera mapCamera;
    private boolean paused = false;
    private PauseScreen pauseScreen;
    private float remainingTime;
    private final float initialTime = 2 * 60f;
    private float blinkAccumulator = 0f;
    private boolean blinkToggle = false;
    private boolean isGameOver = false; // Tracks game over state

    public GameScreen(BomberQuestGame game) {
        this.game = game;
        this.spriteBatch = game.getSpriteBatch();
        this.map = game.getMap();
        this.hud = game.getHud();

        this.mapCamera = new OrthographicCamera();
        this.mapCamera.setToOrtho(false);

        this.pauseScreen = new PauseScreen(game, game.getSkin().getFont("font"));

        this.remainingTime = initialTime;
    }

    @Override
    public void render(float deltaTime) {
        if (!paused) {
            float moveSpeed = 2.5f;
            float vx = 0;
            float vy = 0;

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

            map.getPlayer().updateDirection(vx, vy);
            map.getPlayer().update(deltaTime);

            // Normalize the velocity vector if moving diagonally
            float magnitude = (float) Math.sqrt(vx * vx + vy * vy);
            if (magnitude > 0) {
                vx = (vx / magnitude) * moveSpeed;
                vy = (vy / magnitude) * moveSpeed;
            }

            if (Gdx.input.isKeyJustPressed(KeyBindings.getKey(KeyBindings.PLACE_BOMB))) {
                float px = map.getPlayer().getX();
                float py = map.getPlayer().getY();

                int tileX = (int) Math.floor(px);
                int tileY = (int) Math.floor(py);


                Bomb bomb = new Bomb(map.getWorld(), tileX, tileY, 1, map);
                bomb.setRadius(map.getBlastRadius());
                bomb.startTimer();
                map.addBomb(bomb);
            }

            map.getPlayer().getBody().setLinearVelocity(vx, vy);

            map.tick(deltaTime);

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
            state = blinkToggle ? Hud.PanelState.RED : Hud.PanelState.BLACK;
        } else if (remainingTime < 60) {
            state = Hud.PanelState.RED;
            blinkAccumulator = 0f;
            blinkToggle = false;
        } else {
            state = Hud.PanelState.BLACK;
            blinkAccumulator = 0f;
            blinkToggle = false;
        }

        hud.setPanelState(state);
        hud.setCounts(map.getConcurrentBombCount(), map.getBlastRadius());
        hud.render(timerText);

        if (paused) {
            pauseScreen.render();
        }
    }

    public void setPaused(boolean shouldPause) {
        this.paused = shouldPause;
        if (paused) {
            Gdx.app.log("Pause", "Pausing and setting input processor to PauseScreen stage");
            Gdx.input.setInputProcessor(pauseScreen.getStage());
        } else {
            Gdx.app.log("Pause", "Unpausing; removing PauseScreen stage input processor");
            Gdx.input.setInputProcessor(null);
        }
    }

    public void resumeGame() {
        setPaused(false);
    }

    private void updateCamera() {
        float halfW = mapCamera.viewportWidth * 0.5f;
        float halfH = mapCamera.viewportHeight * 0.5f;
        float cameraLeft = mapCamera.position.x - halfW;
        float cameraRight = mapCamera.position.x + halfW;
        float cameraBottom = mapCamera.position.y - halfH;
        float cameraTop = mapCamera.position.y + halfH;

        float marginX = 0.1f * mapCamera.viewportWidth;
        float marginY = 0.1f * mapCamera.viewportHeight;

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

    private void renderMap() {
        spriteBatch.setProjectionMatrix(mapCamera.combined);
        spriteBatch.begin();

        for (GameObject obj : map.getAllObjects()) {
            if (obj instanceof Drawable drawableObj) {
                draw(spriteBatch, drawableObj);
            }
        }

        for (Enemy enemy : map.getEnemies()) {
            draw(spriteBatch, enemy);
        }

        for (Bomb bomb : map.getBombs()) {
            draw(spriteBatch, bomb);
        }

        if (map.getPlayer() != null) {
            draw(spriteBatch, map.getPlayer());
        }

        spriteBatch.end();
    }

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

    private static void draw(SpriteBatch spriteBatch, Drawable drawable) {
//        if (drawable instanceof GameObject gameObject) {
//            if (gameObject.getBody() == null) return;
//        }

        TextureRegion texture = drawable.getCurrentAppearance();
        float spriteWidthInWorldUnits = (float) texture.getRegionWidth() / TILE_SIZE_PX;
        float spriteHeightInWorldUnits = (float) texture.getRegionHeight() / TILE_SIZE_PX;

        float x = (drawable.getX() - (spriteWidthInWorldUnits / 2)) * TILE_SIZE_PX * SCALE;
        float y = (drawable.getY() - (spriteHeightInWorldUnits / 2)) * TILE_SIZE_PX * SCALE;

        float width = texture.getRegionWidth() * SCALE;
        float height = texture.getRegionHeight() * SCALE;
        spriteBatch.draw(texture, x, y, width, height);
    }

    @Override
    public void resize(int width, int height) {
        mapCamera.setToOrtho(false, width, height);
        hud.resize(width, height);
        pauseScreen.resize(width, height);
    }

    @Override
    public void dispose() {
        pauseScreen.dispose();
    }

    @Override public void pause() {}
    @Override public void resume() {}

    @Override
    public void show() {}

    @Override
    public void hide() {}

    public void setGameOver(boolean b) {}

    public boolean isGameOver() {
        return isGameOver;
    }
}
