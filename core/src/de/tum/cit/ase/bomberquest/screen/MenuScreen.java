package de.tum.cit.ase.bomberquest.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.crashinvaders.vfx.effects.OldTvEffect;
import de.tum.cit.ase.bomberquest.BomberQuestGame;
import de.tum.cit.ase.bomberquest.texture.Textures;
import de.tum.cit.ase.bomberquest.ui.MenuButton;

public class MenuScreen extends BaseScreen {

    private Stage uiStage;

    public MenuScreen(BomberQuestGame game, BitmapFont font) {

        super(game, font, "assets/startScreen/start_background.jpg");


        uiStage = new Stage(backgroundStage.getViewport(), game.getSpriteBatch());


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
        startButton.addListener(event -> {
            if (event.toString().equals("touchDown")) {
                game.setScreen(new FileSelectionScreen(game, font));
            }
            return false;
        });

        MenuButton settingsButton = new MenuButton(
                "Settings",
                desiredWidth, desiredHeight,
                font,
                upDrawable,
                overDrawable
        );
        settingsButton.addListener(event -> {
            if (event.toString().equals("touchDown")) {
                game.setScreen(new SettingsScreen(game, font));
            }
            return false;
        });

        MenuButton quitButton = new MenuButton(
                "Quit Game",
                desiredWidth, desiredHeight,
                font,
                upDrawable,
                overDrawable
        );
        quitButton.addListener(event -> {
            if (event.toString().equals("touchDown")) {
                System.out.println("Quit Game clicked");
                Gdx.app.exit();
            }
            return false;
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
    public void dispose() {

        uiStage.dispose();
        super.dispose();
    }
}
