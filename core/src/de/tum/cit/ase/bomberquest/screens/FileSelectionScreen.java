package de.tum.cit.ase.bomberquest.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import de.tum.cit.ase.bomberquest.BomberQuestGame;
import de.tum.cit.ase.bomberquest.textures.Textures;
import de.tum.cit.ase.bomberquest.ui.MenuButton;

public class FileSelectionScreen extends BaseScreen {

    private final Stage stage;
    private final BitmapFont font;
    private final BomberQuestGame game;
    private ShapeRenderer shapeRenderer;

    public FileSelectionScreen(BomberQuestGame game, BitmapFont font) {

        super(game, font, "assets/startScreen/start_background.jpg");
        this.game = game;
        this.font = font;


        Viewport viewport = new FillViewport(1024, 768, new OrthographicCamera());
        this.stage = new Stage(viewport, game.getSpriteBatch());

        shapeRenderer = new ShapeRenderer();


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

        NinePatchDrawable upDrawable   = new NinePatchDrawable(Textures.BUTTON_LONG_NINEPATCH_OFF);
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

    @Override
    protected void renderContent(float deltaTime) {

        stage.act(deltaTime);


        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.4f); // 40% opacity black
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);


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
        shapeRenderer.dispose();
        super.dispose();
    }
}
