package de.tum.cit.ase.bomberquest.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.tum.cit.ase.bomberquest.BomberQuestGame;
import de.tum.cit.ase.bomberquest.textures.Textures;
import de.tum.cit.ase.bomberquest.ui.MenuButton;

/**
 * Screen for selecting the map files.
 * We enable the overlay in BaseScreen by passing 'true' to the constructor.
 * Uses a ScreenViewport for the UI stage (like in the MenuScreen) to avoid squishing.
 */
public class FileSelectionScreen extends BaseScreen {

    private final Stage stage;
    private final BitmapFont font;
    private final BomberQuestGame game;

    public FileSelectionScreen(BomberQuestGame game, BitmapFont font) {

        super(game, font, "assets/startScreen/start_background.jpg", true);

        this.game = game;
        this.font = font;


        Viewport viewport = new ScreenViewport(new OrthographicCamera());
        this.stage = new Stage(viewport, game.getSpriteBatch());

        Table rootTable = new Table();
        rootTable.setFillParent(true);
        stage.addActor(rootTable);

        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);

        Label headingLabel = new Label("Select your map", labelStyle);
        headingLabel.setFontScale(1.2f);
        rootTable.add(headingLabel).padBottom(20);
        rootTable.row();

        Label chooseMapLabel = new Label("Choose a map", labelStyle);
        rootTable.add(chooseMapLabel).left().padBottom(10);
        rootTable.row();

        Table mapTable = new Table();
        mapTable.defaults().pad(5);

        NinePatchDrawable upDrawable = new NinePatchDrawable(Textures.BUTTON_LONG_NINEPATCH_OFF);
        NinePatchDrawable overDrawable = new NinePatchDrawable(Textures.BUTTON_LONG_NINEPATCH_HOVER);

        FileHandle mapsDirectory = Gdx.files.internal("maps");
        if (mapsDirectory.exists()) {
            for (FileHandle file : mapsDirectory.list()) {
                if (file.extension().equals("properties")) {
                    MenuButton mapButton = new MenuButton(
                            file.name(),
                            400, 70,
                            font,
                            upDrawable,
                            overDrawable
                    );
                    mapButton.setTouchable(Touchable.enabled);
                    mapButton.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            game.loadMap(file.path());
                        }
                    });
                    mapTable.add(mapButton).row();
                }
            }
        }

        rootTable.add(mapTable).padBottom(20);
        rootTable.row();

        Label importLabel = new Label("Import a custom map", labelStyle);
        rootTable.add(importLabel).left().padBottom(10);
        rootTable.row();

        MenuButton importButton = new MenuButton(
                "Select Map from File System",
                600, 70,
                font,
                upDrawable,
                overDrawable
        );
        importButton.setTouchable(Touchable.enabled);
        importButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // TODO: implement file chooser
            }
        });
        rootTable.add(importButton).padBottom(30);
        rootTable.row();

        MenuButton backButton = new MenuButton(
                "Back to Menu",
                400, 70,
                font,
                upDrawable,
                overDrawable
        );
        backButton.setTouchable(Touchable.enabled);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.goToMenu();
            }
        });
        rootTable.add(backButton).padBottom(10);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    /**
     * renderContent is called AFTER the background and overlay are rendered.
     * Just draw the stage here (buttons, labels, etc.).
     */
    @Override
    protected void renderContent(float deltaTime) {
        stage.act(deltaTime);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        super.dispose();
    }
}
