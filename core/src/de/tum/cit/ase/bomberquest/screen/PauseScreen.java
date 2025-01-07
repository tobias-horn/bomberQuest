package de.tum.cit.ase.bomberquest.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;



public class PauseScreen {

    private Stage stage;
    private Label pauseLabel;
    private ShapeRenderer shapeRenderer;


    public PauseScreen(BitmapFont font) {
        shapeRenderer = new ShapeRenderer();
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

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.5f);
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);

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
        shapeRenderer.dispose();
    }
}
