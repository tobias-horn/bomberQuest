package de.tum.cit.ase.bomberquest.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import de.tum.cit.ase.bomberquest.BomberQuestGame;

public class FileSelectionScreen implements Screen {

    private final BomberQuestGame game;
    private final Stage stage;

    public FileSelectionScreen(BomberQuestGame game) {
        this.game = game;
        this.stage = new Stage(new ScreenViewport(), game.getSpriteBatch());

        // Create the UI
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        // Create a vertical group to list files
        VerticalGroup fileListGroup = new VerticalGroup().pad(10).space(10).fill();
        ScrollPane scrollPane = new ScrollPane(fileListGroup, game.getSkin());

        // Load all `.properties` files from the maps directory
        FileHandle mapsDirectory = Gdx.files.internal("maps");
        if (mapsDirectory.exists()) {
            for (FileHandle file : mapsDirectory.list()) {
                if (file.extension().equals("properties")) {
                    // Create a button for each map file
                    TextButton fileButton = new TextButton(file.name(), game.getSkin());

                    // Add a ClickListener to handle user clicks
                    fileButton.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            // Start the game with the selected map
                            game.loadMap(file.path());
                        }
                    });

                    // Add the button to the list group
                    fileListGroup.addActor(fileButton);
                }
            }

        }

        // Add components to the table
        table.add(scrollPane).expand().fill().row();

        // Add a back button
        TextButton backButton = new TextButton("Back to Menu", game.getSkin());
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.goToMenu(); // Go back to the menu only when the button is clicked
            }
        });
        table.add(backButton).pad(10).fillX();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
