package de.tum.cit.ase.bomberquest;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import de.tum.cit.ase.bomberquest.audio.MusicTrack;
import de.tum.cit.ase.bomberquest.map.GameMap;
import de.tum.cit.ase.bomberquest.objects.Player;
import de.tum.cit.ase.bomberquest.screens.GameOverScreen;
import de.tum.cit.ase.bomberquest.screens.GameScreen;
import de.tum.cit.ase.bomberquest.screens.MenuScreen;
import games.spooky.gdx.nativefilechooser.NativeFileChooser;

public class BomberQuestGame extends Game {


    private SpriteBatch spriteBatch;


    private BitmapFont font;


    private Skin skin;


    private final NativeFileChooser fileChooser;


    private GameMap map;


    private Player player;

    private String selectedMap;


    public BomberQuestGame(NativeFileChooser fileChooser) {
        this.fileChooser = fileChooser;
    }


    @Override
    public void create() {
        this.spriteBatch = new SpriteBatch(); // Create SpriteBatch for rendering
        this.skin = new Skin(Gdx.files.internal("skin/craftacular/craftacular-ui.json")); // Load UI skin


        if (MusicTrack.BACKGROUND != null) {
            MusicTrack.BACKGROUND.play();
        }


        FileHandle hardcodedMapFile = Gdx.files.internal("maps/map-1.properties");
        this.map = new GameMap(this, hardcodedMapFile);
        this.font = skin.getFont("font");


        goToMenu();
    }


    public void goToMenu() {
        this.setScreen(new MenuScreen(this, font));
    }


    public void goToGame() {
        this.setScreen(new GameScreen(this));
    }


    public Skin getSkin() {
        return skin;
    }


    public SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }


    public GameMap getMap() {
        return map;
    }


    @Override
    public void setScreen(Screen screen) {
        Screen previousScreen = super.screen;
        super.setScreen(screen);
        if (previousScreen != null) {
            previousScreen.dispose();
        }
    }


    @Override
    public void dispose() {
        getScreen().hide();
        getScreen().dispose();
        spriteBatch.dispose();
        skin.dispose();
    }


    public void loadMap(String mapPath) {
        try {

            FileHandle fileHandle;
            FileHandle internalCheck = Gdx.files.internal(mapPath);
            if (internalCheck.exists()) {
                fileHandle = internalCheck;
            } else {
                fileHandle = Gdx.files.absolute(mapPath);
            }


            this.selectedMap = mapPath;


            this.map = new GameMap(this, fileHandle);


            goToGame();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to load map: " + mapPath);
        }
    }


    public void goToGameOver() {
        setScreen(new GameOverScreen(this, font));
    }


    public void restartGame() {
        Screen currentScreen = getScreen();
        if (currentScreen instanceof GameScreen) {

            ((GameScreen) currentScreen).setGameOver(false);
            currentScreen.dispose();
        }


        if (selectedMap != null) {
            loadMap(selectedMap);
        } else {
            Gdx.app.error("BomberQuestGame", "No map selected to restart the game.");
        }
    }
}
