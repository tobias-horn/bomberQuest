package de.tum.cit.ase.bomberquest.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.Texture;
import de.tum.cit.ase.bomberquest.BomberQuestGame;
import de.tum.cit.ase.bomberquest.audio.MusicTrack;
import de.tum.cit.ase.bomberquest.texture.Textures;
import de.tum.cit.ase.bomberquest.ui.MenuButton;

public class SettingsScreen implements Screen {
    private final Game game;
    private final BitmapFont font;
    private final Stage stage;
    private final float desiredWidth = 400;
    private final float desiredHeight = 70;

    public SettingsScreen(BomberQuestGame game, BitmapFont font) {
        this.game = game;
        this.font = font;

        stage = new Stage();

        NinePatchDrawable upDrawable = new NinePatchDrawable(Textures.BUTTON_LONG_NINEPATCH_OFF);
        NinePatchDrawable overDrawable = new NinePatchDrawable(Textures.BUTTON_LONG_NINEPATCH_HOVER);

        Skin skin = new Skin(Gdx.files.internal("skin/craftacular/craftacular-ui.json"));

        TextureRegionDrawable checkOn = new TextureRegionDrawable(new Texture(Gdx.files.internal("menu/check_disable.png")));
        TextureRegionDrawable checkOff = new TextureRegionDrawable(new Texture(Gdx.files.internal("menu/check_off.png")));

        CheckBox.CheckBoxStyle checkBoxStyle = new CheckBox.CheckBoxStyle();
        checkBoxStyle.checkboxOn = checkOn;
        checkBoxStyle.checkboxOff = checkOff;
        checkBoxStyle.font = skin.getFont("font");
        skin.add("customCheckBox", checkBoxStyle);

        CheckBox muteButton = new CheckBox("Mute Music", skin, "customCheckBox");
        muteButton.setChecked(MusicTrack.BACKGROUND.isMuted());
        muteButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (muteButton.isChecked()) {
                    MusicTrack.BACKGROUND.mute();  // Checked = Muted
                } else {
                    MusicTrack.BACKGROUND.unmute(); // Unchecked = Unmuted
                }
            }
        });


        MenuButton backButton = new MenuButton(
                "Back",
                desiredWidth, desiredHeight,
                font,
                upDrawable,
                overDrawable
        );

        backButton.setTouchable(Touchable.enabled);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game, font));
            }
        });

        Table table = new Table();
        table.setFillParent(true);
        table.add(muteButton).size(desiredWidth, desiredHeight).center().pad(10);
        table.row().pad(10);
        table.add(backButton).size(desiredWidth, desiredHeight).center().pad(10);

        stage.addActor(table);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.2f, 0.3f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
    }
}
