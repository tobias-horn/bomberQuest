package de.tum.cit.ase.bomberquest.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;


public class PauseScreen {

    private Stage stage;
    private Label pauseLabel;


    public PauseScreen(BitmapFont font) {
        stage = new Stage(new ScreenViewport());

        Label.LabelStyle style = new Label.LabelStyle(font, Color.WHITE);


        pauseLabel = new Label("Game Paused. Press ESC to Resume!", style);
        pauseLabel.setFontScale(2);


        Table table = new Table();
        table.setFillParent(true);
        table.center();


        table.add(pauseLabel).center();


        stage.addActor(table);
    }

    /**
     * Renders the pause screen.
     */
    public void render() {
        // Update and draw the stage
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    /**
     * Updates the viewport when the screen is resized.
     *
     * @param width  The new width of the screen.
     * @param height The new height of the screen.
     */
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    /**
     * Disposes of the stage when it's no longer needed.
     */
    public void dispose() {
        stage.dispose();
    }
}
