package de.tum.cit.ase.bomberquest.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import de.tum.cit.ase.bomberquest.BomberQuestGame;
import de.tum.cit.ase.bomberquest.map.GameObject;
import de.tum.cit.ase.bomberquest.texture.Drawable;
import de.tum.cit.ase.bomberquest.map.GameMap;
import de.tum.cit.ase.bomberquest.texture.Textures;


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


    public GameScreen(BomberQuestGame game) {
        this.game = game;
        this.spriteBatch = game.getSpriteBatch();
        this.map = game.getMap();
        this.hud = new Hud(spriteBatch, game.getSkin().getFont("font"));
        // Create and configure the camera for the game view
        this.mapCamera = new OrthographicCamera();
        this.mapCamera.setToOrtho(false);
        // Initialize the PauseScreen
        this.pauseScreen = new PauseScreen(game.getSkin().getFont("font"));
    }


    @Override
    public void render(float deltaTime) {
        float moveSpeed = 3.0f;
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            map.getPlayer().getHitbox().setLinearVelocity(0, moveSpeed);
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            map.getPlayer().getHitbox().setLinearVelocity(0, -moveSpeed);
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            map.getPlayer().getHitbox().setLinearVelocity(-moveSpeed, 0);
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            map.getPlayer().getHitbox().setLinearVelocity(moveSpeed, 0);
        } else {
            map.getPlayer().getHitbox().setLinearVelocity(0, 0); // Stop movement if no key is pressed
        }


        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            paused = !paused;
        }

        if (paused) {
            pauseScreen.render();
            return;
        }


        ScreenUtils.clear(Color.BLACK);
        map.tick(deltaTime);
        updateCamera();
        renderBackground();
        renderMap();
        hud.render();
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
        spriteBatch.setProjectionMatrix(mapCamera.combined); // Ensure camera settings are applied

        spriteBatch.begin(); // Begin the SpriteBatch

        for (GameObject obj : map.getAllObjects()) {
            if (obj instanceof Drawable) {
                Drawable drawableObj = (Drawable) obj;
                System.out.println("Rendering object: " + drawableObj + " at (" + drawableObj.getX() + ", " + drawableObj.getY() + ")");
                draw(spriteBatch, drawableObj);
            }
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
        // Use the same scaling as other game elements
        float tileSizeInWorldUnits = TILE_SIZE_PX * SCALE; // e.g., 32 * 2 = 64 pixels

        // Calculate the starting position based on the camera's position
        float startX = mapCamera.position.x - (mapCamera.viewportWidth / 2);
        float startY = mapCamera.position.y - (mapCamera.viewportHeight / 2);

        // Determine the range of tiles to draw based on the camera's view
        int startTileX = (int)(startX / tileSizeInWorldUnits);
        int startTileY = (int)(startY / tileSizeInWorldUnits);
        int endTileX = (int)((startX + mapCamera.viewportWidth) / tileSizeInWorldUnits) + 1;
        int endTileY = (int)((startY + mapCamera.viewportHeight) / tileSizeInWorldUnits) + 1;

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
     * Draws this object on the screen.
     * The texture will be scaled by the game scale and the tile size.
     * We subtract 0.5 tile so that Box2D center lines up with the sprite center.
     *
     * @param spriteBatch The SpriteBatch to draw with.
     */
    private static void draw(SpriteBatch spriteBatch, Drawable drawable) {
        TextureRegion texture = drawable.getCurrentAppearance();

        // Calculate sprite dimensions in world units
        float spriteWidthInWorldUnits = (float) texture.getRegionWidth() / TILE_SIZE_PX;
        float spriteHeightInWorldUnits = (float) texture.getRegionHeight() / TILE_SIZE_PX;

        // Adjust position to align sprite's bottom center with the Box2D body
        float x = (drawable.getX() - (spriteWidthInWorldUnits / 2)) * TILE_SIZE_PX * SCALE;
        float y = (drawable.getY() - (spriteHeightInWorldUnits / 2)) * TILE_SIZE_PX * SCALE;

        float width = texture.getRegionWidth() * SCALE;
        float height = texture.getRegionHeight() * SCALE;
        spriteBatch.draw(texture, x, y, width, height);
    }

    /**
     * Called when the window is resized.
     * This is where the camera is updated to match the new window size.
     *
     * @param width  The new window width.
     * @param height The new window height.
     */
    @Override
    public void resize(int width, int height) {
        mapCamera.setToOrtho(false, width, height);
        hud.resize(width, height);
        pauseScreen.resize(width, height); // Ensure the pause screen resizes accordingly
    }

    // Unused methods from the Screen interface
    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        pauseScreen.dispose(); // Dispose of the pause screen resources
    }
}
