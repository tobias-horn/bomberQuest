package de.tum.cit.ase.bomberquest.ScreenStates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import de.tum.cit.ase.bomberquest.BomberQuestGame;
import de.tum.cit.ase.bomberquest.screens.BaseScreen;
import de.tum.cit.ase.bomberquest.textures.Textures;
import de.tum.cit.ase.bomberquest.bonusFeatures.ui.MenuButton;

public class MenuScreen extends BaseScreen {

    private Stage uiStage;
    private Image logoImage;

    public MenuScreen(BomberQuestGame game, BitmapFont font) {
        super(game, font, "assets/startScreen/start_background.jpg", false);

        // Create the stage and main UI table
        uiStage = new Stage(new ScreenViewport(), game.getSpriteBatch());
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        uiStage.addActor(mainTable);

        // Create and style the logo
        logoImage = new Image(Textures.LOGO);
        logoImage.setScaling(Scaling.fit);

        // Create the button textures
        float desiredWidth = 400f;
        float desiredHeight = 70f;
        NinePatchDrawable upDrawable   = new NinePatchDrawable(Textures.BUTTON_LONG_NINEPATCH_OFF);
        NinePatchDrawable overDrawable = new NinePatchDrawable(Textures.BUTTON_LONG_NINEPATCH_HOVER);

        // Create the buttons
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
                game.resetTime();
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
                game.setScreen(new SettingsScreen(game, font, false));
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

        // Add the logo in a row above the buttons
        mainTable.row();
        mainTable.add(logoImage)
                .center()
                .height(100f)
                .padBottom(50f);
        mainTable.row();

        // Add the buttons
        mainTable.add(startButton).size(desiredWidth, desiredHeight).center().padBottom(10f);
        mainTable.row();
        mainTable.add(settingsButton).size(desiredWidth, desiredHeight).center().padBottom(10f);
        mainTable.row();
        mainTable.add(quitButton).size(desiredWidth, desiredHeight).center().padBottom(10f);

        Label developerLabel = new Label(
                "Developed by Chris Issa and Tobias Horn",
                new Label.LabelStyle(font, Color.LIGHT_GRAY)
        );


        Table bottomTable = new Table();
        bottomTable.setFillParent(true);

        bottomTable.bottom();


        bottomTable.add(developerLabel)
                .expandX()
                .center()
                .padBottom(20f);


        uiStage.addActor(bottomTable);
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
