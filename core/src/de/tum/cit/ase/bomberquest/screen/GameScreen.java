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


/**
 * The GameScreen class is responsible for rendering the gameplay screen.
 * It handles the game logic and rendering of the game elements.
 */
public class GameScreen implements Screen {
    
    /**
     * The size of a grid cell in pixels.
     * This allows us to think of coordinates in terms of square grid tiles
     * (e.g. x=1, y=1 is the bottom left corner of the map)
     * rather than absolute pixel coordinates.
     */
    public static final int TILE_SIZE_PX = 32;
    
    /**
     * The scale of the game.
     * This is used to make everything in the game look bigger or smaller.
     */
    public static final int SCALE = 1;

    private final BomberQuestGame game;
    private final SpriteBatch spriteBatch;
    private final GameMap map;
    private final Hud hud;
    private final OrthographicCamera mapCamera;
    private boolean paused = false;
    private Stage pauseStage;


    public Stage getPauseStage() {
        return pauseStage;
    }

    public void setPauseStage(Stage pauseStage) {
        this.pauseStage = pauseStage;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    /**
     * Constructor for GameScreen. Sets up the camera and font.
     *
     * @param game The main game class, used to access global resources and methods.
     */
    public GameScreen(BomberQuestGame game) {
        this.game = game;
        this.spriteBatch = game.getSpriteBatch();
        this.map = game.getMap();
        this.hud = new Hud(spriteBatch, game.getSkin().getFont("font"));
        // Create and configure the camera for the game view
        this.mapCamera = new OrthographicCamera();
        this.mapCamera.setToOrtho(false);
    }



    /**
     * The render method is called every frame to render the game.
     * @param deltaTime The time in seconds since the last render.
     */
    @Override
    public void render(float deltaTime) {
        // Handle player movement
        float moveSpeed = 3.0f; // Adjust speed as needed
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

        // Handle Escape key press for pausing or resuming
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (paused) {
                paused = false; // Resume the game
            } else {
                paused = true; // Pause the game
            }
        }

        if (paused) {
            // Draw a pause screen or simply stop updating the game state
            spriteBatch.begin();
            game.getSkin().getFont("font").draw(spriteBatch, "Game Paused. Press ESC to Resume!",
                    mapCamera.position.x - mapCamera.viewportWidth / 2 + 50,
                    mapCamera.position.y + mapCamera.viewportHeight / 2 - 50);
            spriteBatch.end();
            return; // Skip the rest of the rendering logic
        }


        // The rest of your render logic
        ScreenUtils.clear(Color.BLACK);
        map.tick(deltaTime);
        updateCamera();
        renderBackground();
        renderMap();
        hud.render();
    }




    /**
     * Updates the camera to match the current state of the game.
     * Currently, this just centers the camera at the origin.
     */
    private void updateCamera() {
        mapCamera.setToOrtho(false);
        mapCamera.position.x = 3.5f * TILE_SIZE_PX * SCALE;
        mapCamera.position.y = 3.5f * TILE_SIZE_PX * SCALE;
        mapCamera.update(); // This is necessary to apply the changes
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

        spriteBatch.end(); // End the SpriteBatch
    }

    private void renderBackground() {
        // Configure the SpriteBatch to use the camera
        spriteBatch.setProjectionMatrix(mapCamera.combined);
        spriteBatch.begin();

        // Select the background tile
        TextureRegion backgroundTile = Textures.BACKGROUND; // Replace with your desired tile
        float tileSizeInWorldUnits = 0.5f; // Adjust based on your game scaling and grid size

        // Fill the screen with the tile
        for (float x = 0; x < mapCamera.viewportWidth; x += tileSizeInWorldUnits) {
            for (float y = 0; y < mapCamera.viewportHeight; y += tileSizeInWorldUnits) {
                spriteBatch.draw(backgroundTile, x, y, tileSizeInWorldUnits, tileSizeInWorldUnits);
            }
        }

        spriteBatch.end();
    }


    /**
     * Draws this object on the screen.
     * The texture will be scaled by the game scale and the tile size.
     * This should only be called between spriteBatch.begin() and spriteBatch.end(), e.g. in the renderMap() method.
     * @param spriteBatch The SpriteBatch to draw with.
     */
    private static void draw(SpriteBatch spriteBatch, Drawable drawable) {
        TextureRegion texture = drawable.getCurrentAppearance();
        // Drawable coordinates are in tiles, so we need to scale them to pixels
        float x = drawable.getX() * TILE_SIZE_PX * SCALE;
        float y = drawable.getY() * TILE_SIZE_PX * SCALE;
        // Additionally scale everything by the game scale
        float width = texture.getRegionWidth() * SCALE;
        float height = texture.getRegionHeight() * SCALE;
        spriteBatch.draw(texture, x, y, width, height);
    }
    
    /**
     * Called when the window is resized.
     * This is where the camera is updated to match the new window size.
     * @param width The new window width.
     * @param height The new window height.
     */
    @Override
    public void resize(int width, int height) {
        mapCamera.setToOrtho(false);
        hud.resize(width, height);
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
    }

}
