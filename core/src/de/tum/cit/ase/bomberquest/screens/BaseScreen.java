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
 * Base screen for BomberQuest. Renders a background with VFX.
 * Optionally, a transparent overlay can be drawn on top of the background
 * (and below the content of child screens), if showOverlay = true.
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

    public BaseScreen(BomberQuestGame game, BitmapFont font, String backgroundImagePath, boolean showOverlay) {
        this.game = game;
        this.font = font;
        this.showOverlay = showOverlay;

        OrthographicCamera camera = new OrthographicCamera();
        Viewport viewport = new ScreenViewport(camera);
        backgroundStage = new Stage(viewport, game.getSpriteBatch());

        backgroundTexture = new Texture(Gdx.files.internal(backgroundImagePath));
        backgroundTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        Image backgroundImage = new Image(new TextureRegionDrawable(new TextureRegion(backgroundTexture)));
        backgroundImage.setFillParent(true);

        backgroundImage.setScaling(Scaling.fill);
        backgroundStage.addActor(backgroundImage);


        vfxManager = new VfxManager(Pixmap.Format.RGBA8888);
        oldTvEffect = new OldTvEffect();
        vignettingEffect = new VignettingEffect(false);
        crtEffect = new CrtEffect();
        filmGrainEffect = new FilmGrainEffect();

        vfxManager.addEffect(oldTvEffect);
        vfxManager.addEffect(vignettingEffect);
        vfxManager.addEffect(crtEffect);

        overlayShapeRenderer = new ShapeRenderer();
    }

    @Override
    public void render(float deltaTime) {
        float frameTime = Math.min(deltaTime, 0.25f);
        ScreenUtils.clear(Color.BLACK);


        vfxManager.cleanUpBuffers();
        vfxManager.beginInputCapture();
        backgroundStage.act(frameTime);
        backgroundStage.draw();
        vfxManager.endInputCapture();

        vfxManager.applyEffects();


        vfxManager.renderToScreen();


        if (showOverlay) {
            drawOverlay();
        }


        renderContent(frameTime);
    }

    protected abstract void renderContent(float deltaTime);

    @Override
    public void resize(int width, int height) {
        backgroundStage.getViewport().update(width, height, true);
        vfxManager.resize(width, height);
    }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() { }

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


    private void drawOverlay() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        // We draw in screen coordinates, ignoring camera transformations:
        overlayShapeRenderer.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0,
                Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));

        overlayShapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        overlayShapeRenderer.setColor(0, 0, 0, 0.4f); // 40% opacity black
        overlayShapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        overlayShapeRenderer.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);
    }


    public void setShowOverlay(boolean showOverlay) {
        this.showOverlay = showOverlay;
    }

    public boolean isShowOverlay() {
        return showOverlay;
    }
}
