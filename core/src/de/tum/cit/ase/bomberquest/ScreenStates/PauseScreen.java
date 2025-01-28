package de.tum.cit.ase.bomberquest.ScreenStates;

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
import de.tum.cit.ase.bomberquest.screens.GameScreen;
import de.tum.cit.ase.bomberquest.textures.Textures;
import de.tum.cit.ase.bomberquest.bonusFeatures.ui.MenuButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

/**
 * PauseScreen provides a pause overlay in the BomberQuest game.
 * It darkens the background and displays options to resume the game,
 * return to the main menu, access settings, or quit the game.
 * Unlike other screens, it does not extend BaseScreen but manages its own rendering logic.
 */
public class PauseScreen {

    private final BomberQuestGame game;
    private final Stage stage;
    private final Label pauseLabel;
    private final ShapeRenderer shapeRenderer;
    private final BitmapFont font;

    /**
     * Constructs the PauseScreen with the provided game instance and font.
     * Initializes the UI stage, shape renderer, and sets up menu buttons with their respective listeners.
     *
     * @param game The main game instance.
     * @param font The BitmapFont used for rendering text on the buttons.
     */
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

        // Resume Game Button
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
                super.clicked(event, x, y);
            }
        });

        // Return to Main Menu Button
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
                super.clicked(event, x, y);
            }
        });

        // Settings Button
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
                game.setScreen(new SettingsScreen(game, font, true));
                game.setCurrentScreenState(ScreenState.SETTINGS);
                game.setPreviousScreenState(ScreenState.PAUSE);
                super.clicked(event, x, y);
            }
        });

        // Quit Game Button
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
                super.clicked(event, x, y);
            }
        });

        // Layout the buttons in a table
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

    /**
     * Retrieves the Stage used by the PauseScreen for handling UI elements.
     *
     * @return The Stage instance managing the pause menu UI.
     */
    public Stage getStage() {
        return stage;
    }

    /**
     * Renders the pause overlay by drawing a semi-transparent dark background
     * and displaying the UI elements on top.
     * Should be called each frame when the game is paused.
     */
    public void render() {
        // Enable blending for transparency
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        // Draw semi-transparent dark overlay
        shapeRenderer.setProjectionMatrix(new Matrix4().setToOrtho2D(
                0, 0,
                Gdx.graphics.getWidth(), Gdx.graphics.getHeight())
        );

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.5f);
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();

        // Disable blending after drawing
        Gdx.gl.glDisable(GL20.GL_BLEND);

        // Update and draw UI stage
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    /**
     * Handles resizing of the pause screen by updating the viewport of the UI stage.
     *
     * @param width  The new width of the screen.
     * @param height The new height of the screen.
     */
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    /**
     * Disposes of the resources used by the PauseScreen, including the stage and shape renderer.
     * Should be called when the pause screen is no longer needed to free resources.
     */
    public void dispose() {
        stage.dispose();
        shapeRenderer.dispose();
    }
}
