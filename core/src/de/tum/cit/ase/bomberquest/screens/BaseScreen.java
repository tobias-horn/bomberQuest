package de.tum.cit.ase.bomberquest.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.crashinvaders.vfx.VfxManager;
import com.crashinvaders.vfx.effects.CrtEffect;
import com.crashinvaders.vfx.effects.FilmGrainEffect;
import com.crashinvaders.vfx.effects.OldTvEffect;
import com.crashinvaders.vfx.effects.VignettingEffect;
import de.tum.cit.ase.bomberquest.BomberQuestGame;

/**
 * Base screen for BomberQuest. Handles background rendering with visual effects
 * and an optional transparent overlay. Child screens should extend this class
 * and implement the renderContent() method to add specific content.
 */
public abstract class BaseScreen implements Screen {

    protected final BomberQuestGame game;
    protected final BitmapFont font;

    protected Stage backgroundStage;
    protected VfxManager vfxManager;
    protected OldTvEffect oldTvEffect;
    protected Texture backgroundTexture;
    protected VignettingEffect vignettingEffect;
    protected CrtEffect crtEffect;
    protected FilmGrainEffect filmGrainEffect;

    private boolean showOverlay = false;
    private final ShapeRenderer overlayShapeRenderer;

    /**
     * Constructs a new {@code BaseScreen}.
     *
     * @param game                the main game instance
     * @param font                the bitmap font for rendering text
     * @param backgroundImagePath the file path to the background image
     * @param showOverlay         whether to display the transparent overlay
     */
    public BaseScreen(BomberQuestGame game, BitmapFont font, String backgroundImagePath, boolean showOverlay) {
        this.game = game;
        this.font = font;
        this.showOverlay = showOverlay;

        // Initialize camera and viewport
        OrthographicCamera camera = new OrthographicCamera();
        Viewport viewport = new ScreenViewport(camera);
        backgroundStage = new Stage(viewport, game.getSpriteBatch());

        // Load and configure the background texture
        backgroundTexture = new Texture(Gdx.files.internal(backgroundImagePath));
        backgroundTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        // Create and add the background image to the stage
        Image backgroundImage = new Image(new TextureRegionDrawable(new TextureRegion(backgroundTexture)));
        backgroundImage.setFillParent(true);
        backgroundImage.setScaling(Scaling.fill);
        backgroundStage.addActor(backgroundImage);

        // Initialize VFX manager and effects
        vfxManager = new VfxManager(Pixmap.Format.RGBA8888);
        oldTvEffect = new OldTvEffect();
        vignettingEffect = new VignettingEffect(false);
        crtEffect = new CrtEffect();
        filmGrainEffect = new FilmGrainEffect();

        // Add effects to the manager
        vfxManager.addEffect(oldTvEffect);
        vfxManager.addEffect(vignettingEffect);
        vfxManager.addEffect(crtEffect);

        // Initialize the shape renderer for the overlay
        overlayShapeRenderer = new ShapeRenderer();
    }

    /**
     * Renders the screen. This method handles background rendering, applying visual effects,
     * drawing the optional overlay, and delegating additional rendering to child classes.
     *
     * @param deltaTime the time in seconds since the last render
     */
    @Override
    public void render(float deltaTime) {
        float frameTime = Math.min(deltaTime, 0.25f);
        ScreenUtils.clear(Color.BLACK);

        // Prepare VFX manager for rendering
        vfxManager.cleanUpBuffers();
        vfxManager.beginInputCapture();

        // Update and draw the background stage
        backgroundStage.act(frameTime);
        backgroundStage.draw();

        // End input capture and apply effects
        vfxManager.endInputCapture();
        vfxManager.applyEffects();
        vfxManager.renderToScreen();

        // Draw the overlay if enabled
        if (showOverlay) {
            drawOverlay();
        }

        // Render additional content defined by child classes
        renderContent(frameTime);
    }

    /**
     * Renders additional content specific to the child screen.
     * Child classes must implement this method to display their unique content.
     *
     * @param deltaTime the time in seconds since the last render
     */
    protected abstract void renderContent(float deltaTime);

    /**
     * Handles resizing of the screen. Updates the viewport and VFX manager accordingly.
     *
     * @param width  the new width of the screen in pixels
     * @param height the new height of the screen in pixels
     */
    @Override
    public void resize(int width, int height) {
        backgroundStage.getViewport().update(width, height, true);
        vfxManager.resize(width, height);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    /**
     * Releases all resources associated with this screen.
     * This includes disposing of stages, textures, effects, and renderers.
     */
    @Override
    public void dispose() {
        backgroundStage.dispose();
        vfxManager.dispose();
        oldTvEffect.dispose();
        filmGrainEffect.dispose();
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
        overlayShapeRenderer.dispose();
    }

    /**
     * Draws a semi-transparent overlay on top of the screen.
     * The overlay is rendered using screen coordinates and ignores camera transformations.
     */
    private void drawOverlay() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        // Set up orthographic projection for screen coordinates
        overlayShapeRenderer.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0,
                Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));

        // Draw a filled rectangle with 40% opacity black
        overlayShapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        overlayShapeRenderer.setColor(0, 0, 0, 0.4f); // 40% opacity black
        overlayShapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        overlayShapeRenderer.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    /**
     * Sets whether the transparent overlay should be displayed.
     *
     * @param showOverlay {@code true} to display the overlay, {@code false} to hide it
     */
    public void setShowOverlay(boolean showOverlay) {
        this.showOverlay = showOverlay;
    }

    /**
     * Checks whether the transparent overlay is currently displayed.
     *
     * @return {@code true} if the overlay is shown, {@code false} otherwise
     */
    public boolean isShowOverlay() {
        return showOverlay;
    }
}
