package de.tum.cit.ase.bomberquest.ScreenStates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
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
        MenuButton upButton = createKeyBindingButton(KeyBindings.MOVE_UP, upDrawable, overDrawable);
        MenuButton downButton = createKeyBindingButton(KeyBindings.MOVE_DOWN, upDrawable, overDrawable);
        MenuButton leftButton = createKeyBindingButton(KeyBindings.MOVE_LEFT, upDrawable, overDrawable);
        MenuButton rightButton = createKeyBindingButton(KeyBindings.MOVE_RIGHT, upDrawable, overDrawable);
        MenuButton placeBombButton = createKeyBindingButton(KeyBindings.PLACE_BOMB, upDrawable, overDrawable);
        MenuButton shootButton = createKeyBindingButton(KeyBindings.SHOOT_ARROW, upDrawable, overDrawable);
        MenuButton pauseGameButton = createKeyBindingButton(KeyBindings.PAUSE_GAME, upDrawable, overDrawable);

        // Create a label for "Custom Keybindings"
        Label keybindingsLabel = new Label("Custom Keybindings", skin, "default");
        keybindingsLabel.setFontScale(1.2f);

        // Create a table for keybindings with two columns
        Table keybindingsTable = new Table();
        keybindingsTable.left();

        keybindingsTable.add(upButton).size(desiredWidth, desiredHeight).pad(10);
        keybindingsTable.add(downButton).size(desiredWidth, desiredHeight).pad(10);
        keybindingsTable.row();
        keybindingsTable.add(leftButton).size(desiredWidth, desiredHeight).pad(10);
        keybindingsTable.add(rightButton).size(desiredWidth, desiredHeight).pad(10);
        keybindingsTable.row();
        keybindingsTable.add(placeBombButton).size(desiredWidth, desiredHeight).pad(10);
        keybindingsTable.add(shootButton).size(desiredWidth, desiredHeight).pad(10);
        keybindingsTable.row();
        keybindingsTable.add(pauseGameButton).size(desiredWidth, desiredHeight).pad(10);




        CheckBox muteCheckBox = new CheckBox("", skin, "customCheckBox");
        muteCheckBox.setChecked(MusicTrack.BACKGROUND.isMuted());
        muteCheckBox.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (muteCheckBox.isChecked()) {
                    MusicTrack.BACKGROUND.mute();
                    MusicTrack.GAMEPLAY_MUSIC.mute();
                } else {
                    MusicTrack.BACKGROUND.unmute();
                    MusicTrack.GAMEPLAY_MUSIC.unmute();
                }
                super.clicked(event, x, y);
            }
        });

        // Create a label for the mute checkbox
        Label muteLabel = new Label("Mute Music", skin);

        // Create a horizontal table to contain the checkbox and label with padding
        Table muteTable = new Table();
        muteTable.left();
        muteTable.add(muteCheckBox).size(desiredHeight, desiredHeight).padRight(20f);
        muteTable.add(muteLabel).left();

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
                super.clicked(event, x, y);
            }
        });

        // Create the main table
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.center();
        stage.addActor(mainTable);

        // Add the "Custom Keybindings" label
        mainTable.add(keybindingsLabel).colspan(2).padBottom(20f);
        mainTable.row();

        // Add the keybindings table
        mainTable.add(keybindingsTable).colspan(2);
        mainTable.row();

        // Add the muteTable
        mainTable.add(muteTable).colspan(2).padTop(20f);
        mainTable.row();

        // Add the back button
        mainTable.add(backButton).colspan(2).size(desiredWidth, desiredHeight).padTop(20f);
    }

    /**
     * Helper method to create a MenuButton for key bindings.
     *
     * @param action       The action identifier to bind (e.g., MOVE_UP).
     * @param upDrawable   The drawable when the button is not hovered.
     * @param overDrawable The drawable when the button is hovered.
     * @return A configured MenuButton instance.
     */
    private MenuButton createKeyBindingButton(String action, NinePatchDrawable upDrawable, NinePatchDrawable overDrawable) {
        MenuButton button = new MenuButton(
                KeyBindings.getActionName(action) + ": " + Input.Keys.toString(KeyBindings.getKey(action)),
                desiredWidth, desiredHeight,
                font,
                upDrawable,
                overDrawable
        );
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                waitForKeyPress(action, button);
                super.clicked(event, x, y);
            }
        });
        return button;
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
                Gdx.input.setInputProcessor(stage);
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
