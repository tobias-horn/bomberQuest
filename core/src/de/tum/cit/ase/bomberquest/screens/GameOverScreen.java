package de.tum.cit.ase.bomberquest.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import de.tum.cit.ase.bomberquest.BomberQuestGame;

/**
 * GameOverScreen displays a "Game Over" message and provides options to restart or return to the main menu.
 */
public class GameOverScreen implements Screen {
    private final BomberQuestGame game;
    private final Skin skin;
    private Stage stage;

    /**
     * Constructor for GameOverScreen.
     *
     * @param bomberQuestGame The main game instance.
     * @param skin            The UI skin.
     */
    public GameOverScreen(BomberQuestGame bomberQuestGame, Skin skin) {
        this.game = bomberQuestGame;
        this.skin = skin;
        this.stage = new Stage();

        // Create a table to layout UI elements
        Table table = new Table();
        table.setFillParent(true);
        table.center();

        // Create "Game Over" label
        Label gameOverLabel = new Label("Game Over", skin);
        gameOverLabel.setFontScale(2);
        table.add(gameOverLabel).padBottom(50);
        table.row();

        // Create "Restart Game" button
        TextButton restartButton = new TextButton("Restart Game", skin);
        restartButton.setSize(200, 50);
        table.add(restartButton).size(200, 50).padBottom(20);
        table.row();

        // Create "Return to Menu" button
        TextButton menuButton = new TextButton("Return to Menu", skin);
        menuButton.setSize(200, 50);
        table.add(menuButton).size(200, 50);

        // Add listeners to buttons
        restartButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.restartGame(); // Ensure this method resets the game correctly
            }
        });

        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.goToMenu();
            }
        });

        // Add the table to the stage
        stage.addActor(table);
    }

    /**
     * Called when this screen becomes the current screen for a Game.
     */
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    /**
     * Renders the screen. Clears the screen with a solid color and draws UI elements.
     *
     * @param delta The time in seconds since the last render.
     */
    @Override
    public void render(float delta) {
        // Clear the screen with a dark gray color
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update and draw the stage
        stage.act(delta);
        stage.draw();
    }

    /**
     * Handles the resizing of the game window.
     *
     * @param width  The new width in pixels.
     * @param height The new height in pixels.
     */
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
        // Handle game pause if necessary
    }

    @Override
    public void resume() {
        // Handle game resume if necessary
    }

    @Override
    public void hide() {
        // Called when this screen is no longer the current screen
    }

    /**
     * Releases all resources of this screen.
     */
    @Override
    public void dispose() {
        stage.dispose();
    }
}
