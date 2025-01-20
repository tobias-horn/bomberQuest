package de.tum.cit.ase.bomberquest.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import de.tum.cit.ase.bomberquest.BomberQuestGame;
import de.tum.cit.ase.bomberquest.audio.MusicTrack;
import de.tum.cit.ase.bomberquest.textures.Textures;
import de.tum.cit.ase.bomberquest.ui.MenuButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

/**
 * PauseScreen does NOT extend BaseScreen, but manually replicates the same
 * overlay logic with a ShapeRenderer to darken the screen behind the UI.
 */
public class PauseScreen {

    private final BomberQuestGame game;
    private final Stage stage;
    private final Label pauseLabel;
    private final ShapeRenderer shapeRenderer;
    private final BitmapFont font;

    public PauseScreen(BomberQuestGame game, BitmapFont font) {
        this.game = game;
        this.font = font;


        shapeRenderer = new ShapeRenderer();


        stage = new Stage(new ScreenViewport());

        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        pauseLabel = new Label("Game Paused", labelStyle);
        pauseLabel.setFontScale(2);

        NinePatchDrawable upDrawable = new NinePatchDrawable(Textures.BUTTON_LONG_NINEPATCH_OFF);
        NinePatchDrawable overDrawable = new NinePatchDrawable(Textures.BUTTON_LONG_NINEPATCH_HOVER);

        float desiredWidth = 400;
        float desiredHeight = 70;

        MenuButton resumeButton = new MenuButton(
                "Resume Game",
                desiredWidth, desiredHeight,
                font,
                upDrawable,
                overDrawable
        );
        resumeButton.setTouchable(Touchable.enabled);
        resumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Resume the game from pause
                ((GameScreen) game.getScreen()).resumeGame();
            }
        });

        MenuButton menuButton = new MenuButton(
                "To main menu",
                desiredWidth, desiredHeight,
                font,
                upDrawable,
                overDrawable
        );
        menuButton.setTouchable(Touchable.enabled);
        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("PauseScreen", "To main menu clicked");
                game.setScreen(new MenuScreen(game, font));
                game.setScreenWithState(ScreenState.MENU);

            }
        });

        MenuButton settingsButton = new MenuButton(
                "Settings",
                desiredWidth, desiredHeight,
                font,
                upDrawable,
                overDrawable
        );
        settingsButton.setTouchable(Touchable.enabled);
        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("PauseScreen", "Settings button clicked");
                game.setScreen(new SettingsScreen(game, font));
                game.setScreenWithState(ScreenState.SETTINGS);
            }
        });

        MenuButton quitButton = new MenuButton(
                "Quit Game",
                desiredWidth, desiredHeight,
                font,
                upDrawable,
                overDrawable
        );
        quitButton.setTouchable(Touchable.enabled);
        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("PauseScreen", "Quit Game clicked");
                Gdx.app.exit();
            }
        });

        Table table = new Table();
        table.setFillParent(true);
        table.center();


        table.add(pauseLabel).center().padBottom(50);
        table.row();
        table.add(resumeButton).size(desiredWidth, desiredHeight).center().padBottom(10);
        table.row();
        table.add(menuButton).size(desiredWidth, desiredHeight).center().padBottom(10);
        table.row();
        table.add(settingsButton).size(desiredWidth, desiredHeight).center().padBottom(10);
        table.row();
        table.add(quitButton).size(desiredWidth, desiredHeight).center();

        stage.addActor(table);
    }

    public Stage getStage() {
        return stage;
    }


    public void render() {

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);


        shapeRenderer.setProjectionMatrix(new Matrix4().setToOrtho2D(
                0, 0,
                Gdx.graphics.getWidth(), Gdx.graphics.getHeight())
        );

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.5f);
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);


        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public void dispose() {
        stage.dispose();
        shapeRenderer.dispose();
    }
}
