package de.tum.cit.ase.bomberquest.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import de.tum.cit.ase.bomberquest.BomberQuestGame;
import de.tum.cit.ase.bomberquest.textures.Textures;
import de.tum.cit.ase.bomberquest.bonusFeatures.ui.MenuButton;

/**
 * MenuScreen displays the main menu of the BomberQuest game.
 * It provides options to start a new game, access settings, or quit the game.
 * The screen is constructed using a Stage and Table for layout management.
 */
public class MenuScreen extends BaseScreen {

    private Stage uiStage;

    /**
     * Constructs the MenuScreen with the provided game instance and font.
     * Initializes the UI stage and sets up menu buttons with their respective listeners.
     *
     * @param game The main game instance.
     * @param font The BitmapFont used for rendering text on the buttons.
     */
    public MenuScreen(BomberQuestGame game, BitmapFont font) {
        super(game, font, "assets/startScreen/start_background.jpg", false);

        uiStage = new Stage(new ScreenViewport(), game.getSpriteBatch());

        Table table = new Table();
        table.setFillParent(true);
        uiStage.addActor(table);

        float desiredWidth = 400f;
        float desiredHeight = 70f;
        NinePatchDrawable upDrawable   = new NinePatchDrawable(Textures.BUTTON_LONG_NINEPATCH_OFF);
        NinePatchDrawable overDrawable = new NinePatchDrawable(Textures.BUTTON_LONG_NINEPATCH_HOVER);

        MenuButton startButton = new MenuButton(
                "Start Game",
                desiredWidth, desiredHeight,
                font,
                upDrawable,
                overDrawable
        );
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new FileSelectionScreen(game, font));
                super.clicked(event, x, y);
            }
        });

        MenuButton settingsButton = new MenuButton(
                "Settings",
                desiredWidth, desiredHeight,
                font,
                upDrawable,
                overDrawable
        );
        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new SettingsScreen(game, font));
                game.setPreviousScreenState(ScreenState.MENU);
                game.setCurrentScreenState(ScreenState.SETTINGS);
                super.clicked(event, x, y);
            }
        });

        MenuButton quitButton = new MenuButton(
                "Quit Game",
                desiredWidth, desiredHeight,
                font,
                upDrawable,
                overDrawable
        );
        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Quit Game clicked");
                Gdx.app.exit();
                super.clicked(event, x, y);
            }
        });

        table.row();
        table.add(startButton).size(desiredWidth, desiredHeight).center().padBottom(10f);
        table.row();
        table.add(settingsButton).size(desiredWidth, desiredHeight).center().padBottom(10f);
        table.row();
        table.add(quitButton).size(desiredWidth, desiredHeight).center().padBottom(10f);
    }

    /**
     * Sets the input processor to the UI stage when the screen is shown.
     */
    @Override
    public void show() {
        Gdx.input.setInputProcessor(uiStage);
    }

    /**
     * Renders the UI stage by updating and drawing its actors.
     *
     * @param deltaTime The time elapsed since the last render.
     */
    @Override
    protected void renderContent(float deltaTime) {
        uiStage.act(deltaTime);
        uiStage.draw();
    }

    /**
     * Handles resizing of the screen by updating the viewport of the UI stage.
     *
     * @param width  The new width of the screen.
     * @param height The new height of the screen.
     */
    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        uiStage.getViewport().update(width, height, true);
    }

    /**
     * Disposes of the UI stage and any other resources when the screen is no longer needed.
     */
    @Override
    public void dispose() {
        uiStage.dispose();
        super.dispose();
    }
}
