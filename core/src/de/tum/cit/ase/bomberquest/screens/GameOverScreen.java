package de.tum.cit.ase.bomberquest.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import de.tum.cit.ase.bomberquest.BomberQuestGame;
import de.tum.cit.ase.bomberquest.textures.Textures;
import de.tum.cit.ase.bomberquest.ui.MenuButton;

import java.util.Random;

/**
 * GameOverScreen displays a "Game Over" message and a random sub-message.
 * It also provides options to restart or return to the main menu.
 */
public class GameOverScreen extends BaseScreen {

    private final Stage uiStage;
    private final String gameOverMessage;

    private static final String[] MESSAGES = {
            "Boom! And just like that... Game Over.",
            "Out of lives, but not out of spirit!",
            "You got blown away—literally!",
            "The explosion was the easy part... surviving wasn't.",
            "You went out with a bang... Game Over.",
            "Better luck next time, demolition expert.",
            "Kaboom! That's all for now.",
            "Looks like you dropped the bomb... on yourself.",
            "Game Over. You just couldn't handle the pressure.",
            "The fuse ran out... and so did you.",
            "Boom, bust, and back to the start.",
            "You’re toast. Literally.",
            "No guts, no glory. You had the guts—just not the glory.",
            "Your plans backfired... explosively.",
            "That was dynamite! But also... Game Over.",
            "Another try? Or are you just blown away?",
            "Your bombing skills need some defusing.",
            "Looks like you were caught in the crossfire.",
            "Your adventure ends here. For now...",
            "Explosive ending, but not the one you wanted."
    };

    /**
     * Constructs the GameOverScreen with the provided game instance and font.
     *
     * @param game The main game instance.
     * @param font The BitmapFont to use for text rendering.
     */
    public GameOverScreen(BomberQuestGame game, BitmapFont font) {

        super(game, font, "assets/startScreen/start_background.jpg", true);

        Random random = new Random();
        this.gameOverMessage = MESSAGES[random.nextInt(MESSAGES.length)];

        this.uiStage = new Stage(new ScreenViewport(), game.getSpriteBatch());

        Table table = new Table();
        table.setFillParent(true);
        table.center();

        Label gameOverLabel = new Label("GAME OVER", new Label.LabelStyle(font, font.getColor()));
        gameOverLabel.setFontScale(2.0f);
        table.add(gameOverLabel).padBottom(40f);
        table.row();

        Label messageLabel = new Label(gameOverMessage, new Label.LabelStyle(font, font.getColor()));
        table.add(messageLabel).padBottom(50f);
        table.row();

        // --- New logic for final score and high score ---
        int finalScore = 0;
        int highScore  = 0;
        if (game.getMap() != null && game.getMap().getScore() != null) {




            finalScore = game.getMap().getScore().getScore();

        }

        // Display final score
        Label scoreLabel = new Label("Score: " + finalScore, new Label.LabelStyle(font, font.getColor()));
        table.add(scoreLabel).padBottom(30f);
        table.row();

        // Display high score
        Label highScoreLabel = new Label("High Score: " + highScore, new Label.LabelStyle(font, font.getColor()));
        table.add(highScoreLabel).padBottom(40f);
        table.row();
        // --- End new logic ---

        float desiredWidth = 400f;
        float desiredHeight = 70f;
        NinePatchDrawable upDrawable   = new NinePatchDrawable(Textures.BUTTON_LONG_NINEPATCH_OFF);
        NinePatchDrawable overDrawable = new NinePatchDrawable(Textures.BUTTON_LONG_NINEPATCH_HOVER);

        MenuButton restartButton = new MenuButton(
                "Restart Game",
                desiredWidth, desiredHeight,
                font,
                upDrawable,
                overDrawable
        );
        restartButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.restartGame();  // Make sure this method resets the game properly
                super.clicked(event, x, y);
            }
        });
        table.add(restartButton).size(desiredWidth, desiredHeight).padBottom(15f);
        table.row();

        MenuButton menuButton = new MenuButton(
                "Return to Menu",
                desiredWidth, desiredHeight,
                font,
                upDrawable,
                overDrawable
        );
        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setCurrentScreenState(ScreenState.MENU);
                game.setPreviousScreenState(ScreenState.GAME_OVER);
                game.goToMenu();
                super.clicked(event, x, y);
            }
        });
        table.add(menuButton).size(desiredWidth, desiredHeight);

        uiStage.addActor(table);
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
