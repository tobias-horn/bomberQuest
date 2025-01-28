package de.tum.cit.ase.bomberquest.ScreenStates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.tum.cit.ase.bomberquest.BomberQuestGame;
import de.tum.cit.ase.bomberquest.audio.MusicTrack;
import de.tum.cit.ase.bomberquest.screens.BaseScreen;
import de.tum.cit.ase.bomberquest.screens.GameScreen;
import de.tum.cit.ase.bomberquest.textures.Textures;
import de.tum.cit.ase.bomberquest.bonusFeatures.ui.KeyBindings;
import de.tum.cit.ase.bomberquest.bonusFeatures.ui.MenuButton;

/**
 * SettingsScreen allows players to configure key bindings and toggle music settings.
 * It provides options to rebind movement and action keys, mute/unmute music,
 * and navigate back to the previous screen.
 * The screen is constructed using a Stage and Table for layout management.
 */
public class SettingsScreen extends BaseScreen {

    private final Stage stage;
    private final BitmapFont font;
    private final BomberQuestGame game;
    private final float desiredWidth = 400;
    private final float desiredHeight = 70;
    private boolean fromPause;

    /**
     * Constructs the SettingsScreen with the provided game instance and font.
     * Initializes the UI stage, sets up key binding buttons, mute checkbox, and back button.
     *
     * @param game The main game instance.
     * @param font The BitmapFont used for rendering text on the buttons.
     */

    public SettingsScreen(BomberQuestGame game, BitmapFont font, boolean fromPause) {
        super(game, font, "assets/startScreen/start_background.jpg", true);
        this.fromPause = fromPause;

        this.game = game;
        this.font = font;

        Viewport viewport = new ScreenViewport();
        this.stage = new Stage(viewport, game.getSpriteBatch());

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

        // Create buttons for key bindings
        MenuButton upButton = new MenuButton(
                KeyBindings.getActionName(KeyBindings.MOVE_UP) + ": " + Input.Keys.toString(KeyBindings.getKey(KeyBindings.MOVE_UP)),
                desiredWidth, desiredHeight,
                font,
                upDrawable,
                overDrawable
        );
        upButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                waitForKeyPress(KeyBindings.MOVE_UP, upButton);
            }
        });

        MenuButton downButton = new MenuButton(
                KeyBindings.getActionName(KeyBindings.MOVE_DOWN) + ": " + Input.Keys.toString(KeyBindings.getKey(KeyBindings.MOVE_DOWN)),
                desiredWidth, desiredHeight,
                font,
                upDrawable,
                overDrawable
        );
        downButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                waitForKeyPress(KeyBindings.MOVE_DOWN, downButton);
            }
        });

        MenuButton leftButton = new MenuButton(
                KeyBindings.getActionName(KeyBindings.MOVE_LEFT) + ": " + Input.Keys.toString(KeyBindings.getKey(KeyBindings.MOVE_LEFT)),
                desiredWidth, desiredHeight,
                font,
                upDrawable,
                overDrawable
        );
        leftButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                waitForKeyPress(KeyBindings.MOVE_LEFT, leftButton);
            }
        });

        MenuButton rightButton = new MenuButton(
                KeyBindings.getActionName(KeyBindings.MOVE_RIGHT) + ": " + Input.Keys.toString(KeyBindings.getKey(KeyBindings.MOVE_RIGHT)),
                desiredWidth, desiredHeight,
                font,
                upDrawable,
                overDrawable
        );
        rightButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                waitForKeyPress(KeyBindings.MOVE_RIGHT, rightButton);
            }
        });

        MenuButton placeBombButton = new MenuButton(
                KeyBindings.getActionName(KeyBindings.PLACE_BOMB) + ": " + Input.Keys.toString(KeyBindings.getKey(KeyBindings.PLACE_BOMB)),
                desiredWidth, desiredHeight,
                font,
                upDrawable,
                overDrawable
        );
        placeBombButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                waitForKeyPress(KeyBindings.PLACE_BOMB, placeBombButton);
            }
        });

        MenuButton pauseGameButton = new MenuButton(
                KeyBindings.getActionName(KeyBindings.PAUSE_GAME) + ": " + Input.Keys.toString(KeyBindings.getKey(KeyBindings.PAUSE_GAME)),
                desiredWidth, desiredHeight,
                font,
                upDrawable,
                overDrawable
        );
        pauseGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                waitForKeyPress(KeyBindings.PAUSE_GAME, pauseGameButton);
            }
        });

        // Create mute music checkbox
        CheckBox muteButton = new CheckBox("Mute Music", skin, "customCheckBox");
        muteButton.setChecked(MusicTrack.BACKGROUND.isMuted());
        muteButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (muteButton.isChecked()) {
                    MusicTrack.BACKGROUND.mute();
                } else {
                    MusicTrack.BACKGROUND.unmute();
                }
            }
        });

        // Create back button
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
                if (fromPause) {
                    game.setScreenWithState(ScreenState.GAME);
                    ((GameScreen) game.getScreen()).setPaused(true);
                } else {
                    game.goBack();
                }
                game.setPreviousScreenState(ScreenState.SETTINGS);
            }
        });

        // Layout the buttons in a table
        Table table = new Table();
        table.setFillParent(true);
        table.row().pad(10);
        table.add(upButton).size(desiredWidth, desiredHeight).center().pad(10);
        table.row().pad(10);
        table.add(downButton).size(desiredWidth, desiredHeight).center().pad(10);
        table.row().pad(10);
        table.add(leftButton).size(desiredWidth, desiredHeight).center().pad(10);
        table.row().pad(10);
        table.add(rightButton).size(desiredWidth, desiredHeight).center().pad(10);
        table.row().pad(10);
        table.add(placeBombButton).size(desiredWidth, desiredHeight).center().pad(10);
        table.row().pad(10);
        table.add(pauseGameButton).size(desiredWidth, desiredHeight).center().pad(10);
        table.row().pad(10);
        table.add(muteButton).size(desiredWidth, desiredHeight).center().pad(10);
        table.row().pad(10);
        table.add(backButton).size(desiredWidth, desiredHeight).center().pad(10);

        stage.addActor(table);
    }

    /**
     * Waits for the user to press a key to rebind the specified action.
     * Updates the button text to reflect the new key binding.
     *
     * @param action The action identifier to rebind (e.g., MOVE_UP).
     * @param button The MenuButton that triggered the key binding change.
     */
    private void waitForKeyPress(String action, MenuButton button) {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                KeyBindings.setKey(action, keycode);
                // Update button text to display the friendly action name and key
                button.setText(KeyBindings.getActionName(action) + ": " + Input.Keys.toString(keycode));
                Gdx.input.setInputProcessor(stage); // Restore input processor
                return true;
            }
        });
    }

    /**
     * Sets the input processor to the UI stage when the screen is shown.
     */
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    /**
     * Renders the UI stage by updating and drawing its actors.
     *
     * @param deltaTime The time elapsed since the last render.
     */
    @Override
    protected void renderContent(float deltaTime) {

        Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);

        stage.act(deltaTime);
        stage.draw();
    }

    /**
     * Handles resizing of the screen by updating the viewport of the UI stage.
     *
     * @param width  The new width of the screen.
     * @param height The new height of the screen.
     */
    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        stage.getViewport().update(width, height, true);
    }

    /**
     * Disposes of the UI stage and any other resources when the screen is no longer needed.
     */
    @Override
    public void dispose() {
        stage.dispose();
        super.dispose();
    }
}
