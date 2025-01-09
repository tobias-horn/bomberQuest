package de.tum.cit.ase.bomberquest.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.tum.cit.ase.bomberquest.BomberQuestGame;

import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import de.tum.cit.ase.bomberquest.texture.Textures;
import de.tum.cit.ase.bomberquest.ui.MenuButton;

public class MenuScreen implements Screen {

    private final Stage stage;
    private Texture backgroundTexture;
    private BitmapFont font;

    public MenuScreen(BomberQuestGame game, BitmapFont font) {

        var camera = new OrthographicCamera();
        this.font = font;

        Viewport viewport = new FillViewport(1024, 768, camera);
        stage = new Stage(viewport, game.getSpriteBatch());


        backgroundTexture = new Texture(Gdx.files.internal("assets/startScreen/start_background.jpg"));
        backgroundTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);


        Image backgroundImage = new Image(new TextureRegionDrawable(new TextureRegion(backgroundTexture)));

        backgroundImage.setFillParent(true);

        backgroundImage.setScaling(Scaling.fill);


        stage.addActor(backgroundImage);


        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);


        NinePatchDrawable upDrawable   = new NinePatchDrawable(Textures.BUTTON_LONG_NINEPATCH_OFF);
        NinePatchDrawable overDrawable = new NinePatchDrawable(Textures.BUTTON_LONG_NINEPATCH_HOVER);


        float desiredWidth = 400;
        float desiredHeight = 70;


        MenuButton startButton = new MenuButton(
                "Start Game",
                desiredWidth, desiredHeight,
                font,
                upDrawable,
                overDrawable
        );

        startButton.setTouchable(Touchable.enabled);

        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                game.setScreen(new FileSelectionScreen(game, font));
            }
        });

        //Settings Button
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
                System.out.println("Settings button clicked!");
                game.setScreen(new SettingsScreen(game, font));
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


        table.row();

        table.add(startButton)
                .size(desiredWidth, desiredHeight)
                .center()
                .padBottom(10);
        table.row();

        table.add(settingsButton)
                .size(desiredWidth, desiredHeight)
                .center()
                .padBottom(10);
        table.row();

        table.add(quitButton)
                .size(desiredWidth, desiredHeight)
                .center()
                .padBottom(10);
        table.row();
    }

    @Override
    public void render(float deltaTime) {
        float frameTime = Math.min(deltaTime, 0.250f);
        ScreenUtils.clear(Color.BLACK);

        stage.act(frameTime);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        // Let the FillViewport handle resizing
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }


    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
