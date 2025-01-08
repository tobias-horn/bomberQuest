package de.tum.cit.ase.bomberquest.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import de.tum.cit.ase.bomberquest.BomberQuestGame;
import de.tum.cit.ase.bomberquest.texture.Textures;

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

        NinePatch ninePatchOff = Textures.BUTTON_LONG_NINEPATCH_OFF;
        NinePatch ninePatchHover = Textures.BUTTON_LONG_NINEPATCH_HOVER;

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.up = new NinePatchDrawable(ninePatchOff);
        buttonStyle.over = new NinePatchDrawable(ninePatchHover);
        buttonStyle.font = font;
        buttonStyle.fontColor = Color.WHITE;

        TextButton resumeButton = new TextButton("Resume Game", buttonStyle);
        resumeButton.setTouchable(Touchable.enabled);


        resumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Resume Game clicked");
                Gdx.app.log("PauseScreen", "Resume Game clicked");
                // calls GameScreen.resumeGame()
                ((GameScreen) game.getScreen()).resumeGame();
            }
        });

        TextButton menuButton = new TextButton("To main menu", buttonStyle);
        menuButton.setTouchable(Touchable.enabled);


        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("To main menu clicked");
                Gdx.app.log("PauseScreen", "To main menu clicked");
                // Switch to MenuScreen
                game.setScreen(new MenuScreen(game));
            }
        });

        float desiredWidth = 400;
        float desiredHeight = 70;

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
