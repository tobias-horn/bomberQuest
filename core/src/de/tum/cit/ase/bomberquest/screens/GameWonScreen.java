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

public class GameWonScreen extends BaseScreen {

    private final Stage uiStage;
    private final String winMessage;

    private static final String[] WIN_MESSAGES = {
            "You’ve bombed your way to glory! Nicely done!",
            "The enemies are toast, and you’re the master chef!",
            "Congratulations! You turned chaos into victory. Explosively.",
            "Boom! You’ve won! (And so did your ego.)",
            "Exit unlocked, enemies defeated, and your skills undefeated!",
            "You came, you saw, you blew everything up. A true hero!",
            "Game won! Those enemies never stood a chance against your brilliance.",
            "The exit is yours, and so is eternal bragging rights.",
            "Kaboom! They’re gone, you’re still here. That’s a win!",
            "Victory is yours! The enemies are regretting their life choices.",
            "You’ve made explosions look like art. Michelangelo would be proud!",
            "Congratulations! You bombed your way into the hall of fame.",
            "Boom-tastic! You’re officially the Sultan of the Exit.",
            "Game over… for them! Victory is all yours!",
            "Explosions, enemies, and exits. You’ve mastered the trifecta!",
            "Enemy K.O. and exit unlocked! You're the bomb!",
            "The exit says ‘Thank You’ for finding it. Great work!",
            "Victory! Somewhere, an enemy is shedding a pixelated tear.",
            "Game won! You’ve proven that explosions solve everything.",
            "The exit is clear, the enemies are gone, and you’re unstoppable!"
    };

    public GameWonScreen(BomberQuestGame game, BitmapFont font) {
        super(game, font, "assets/startScreen/start_background.jpg", true);

        Random random = new Random();
        this.winMessage = WIN_MESSAGES[random.nextInt(WIN_MESSAGES.length)];

        this.uiStage = new Stage(new ScreenViewport(), game.getSpriteBatch());


        Table table = new Table();
        table.setFillParent(true);
        table.center();


        Label.LabelStyle labelStyle = new Label.LabelStyle(font, font.getColor());
        Label wonLabel = new Label("YOU WON!", labelStyle);
        wonLabel.setFontScale(2.0f);
        table.add(wonLabel).padBottom(40f);
        table.row();


        Label messageLabel = new Label(winMessage, labelStyle);
        table.add(messageLabel).padBottom(50f);
        table.row();


        float desiredWidth = 400f;
        float desiredHeight = 70f;
        NinePatchDrawable upDrawable = new NinePatchDrawable(Textures.BUTTON_LONG_NINEPATCH_OFF);
        NinePatchDrawable overDrawable = new NinePatchDrawable(Textures.BUTTON_LONG_NINEPATCH_HOVER);

        MenuButton restartButton = new MenuButton(
                "Start New Game",
                desiredWidth, desiredHeight,
                font, upDrawable, overDrawable
        );
        restartButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.restartGame();
                super.clicked(event, x, y);
            }
        });
        table.add(restartButton).size(desiredWidth, desiredHeight).padBottom(15f);
        table.row();

        MenuButton menuButton = new MenuButton(
                "Return to Menu",
                desiredWidth, desiredHeight,
                font, upDrawable, overDrawable
        );
        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.goToMenu();
                super.clicked(event, x, y);
            }
        });
        table.add(menuButton).size(desiredWidth, desiredHeight);

        uiStage.addActor(table);
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
