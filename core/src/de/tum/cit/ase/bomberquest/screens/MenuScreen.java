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
import de.tum.cit.ase.bomberquest.ui.MenuButton;

public class MenuScreen extends BaseScreen {

    private Stage uiStage;

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

    @Override
    public void show() {
        Gdx.input.setInputProcessor(uiStage);
    }

    @Override
    protected void renderContent(float deltaTime) {
        uiStage.act(deltaTime);
        uiStage.draw();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        uiStage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        uiStage.dispose();
        super.dispose();
    }
}
