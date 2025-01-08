package de.tum.cit.ase.bomberquest.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import de.tum.cit.ase.bomberquest.BomberQuestGame;
import de.tum.cit.ase.bomberquest.texture.Textures;
import de.tum.cit.ase.bomberquest.ui.MenuButton;  // <-- Import our custom button
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

public class PauseScreen {

    private final BomberQuestGame game;
    private Stage stage;
    private Label pauseLabel;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;

    public PauseScreen(BomberQuestGame game, BitmapFont font) {
        this.game = game;
        this.font = font;
        shapeRenderer = new ShapeRenderer();
        stage = new Stage(new ScreenViewport());


        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        pauseLabel = new Label("Game Paused", labelStyle);
        pauseLabel.setFontScale(2);


        NinePatchDrawable upDrawable   = new NinePatchDrawable(Textures.BUTTON_LONG_NINEPATCH_OFF);
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
                System.out.println("To main menu clicked");
                Gdx.app.log("PauseScreen", "To main menu clicked");
                game.setScreen(new MenuScreen(game, font)); // go to main menu
            }
        });

        //Quit Game button
        TextButton quitButton = new MenuButton("Quit Game",
                desiredWidth, desiredHeight,
                font,
                upDrawable,
                overDrawable);
        quitButton.setTouchable(Touchable.enabled);

        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Quit Game clicked");
                Gdx.app.log("PauseScreen", "Quit Game clicked");
                // Exit the application
                Gdx.app.exit();
            }
        });




        Table table = new Table();
        table.setFillParent(true);
        table.center();

        table.add(pauseLabel).center().padBottom(50);
        table.row();


        table.add(resumeButton)
                .size(desiredWidth, desiredHeight)
                .center()
                .padBottom(20);
        table.row();

        table.add(menuButton)
                .size(desiredWidth, desiredHeight)
                .center()
                .padBottom(20);
        table.row();

        table.add(quitButton)
                .size(desiredWidth, desiredHeight)
                .center();

        stage.addActor(table);
    }

    public Stage getStage() {
        return stage;
    }

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

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public void dispose() {
        stage.dispose();
        shapeRenderer.dispose();
    }
}
