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
import de.tum.cit.ase.bomberquest.map.MapParser;
import de.tum.cit.ase.bomberquest.map.Player;
import de.tum.cit.ase.bomberquest.screen.GameScreen;
import de.tum.cit.ase.bomberquest.screen.MenuScreen;
import games.spooky.gdx.nativefilechooser.NativeFileChooser;

/**
 * The BomberQuestGame class represents the core of the Bomber Quest game.
 * It manages the screens and global resources like SpriteBatch and Skin.
 */
public class BomberQuestGame extends Game {

    /**
     * Sprite Batch for rendering game elements.
     * This eats a lot of memory, so we only want one of these.
     */
    private SpriteBatch spriteBatch;
    private BitmapFont font;

    /** The game's UI skin. This is used to style the game's UI elements. */
    private Skin skin;
    
    /**
     * The file chooser for loading map files from the user's computer.
     * This will give you access to a {@link com.badlogic.gdx.files.FileHandle} object,
     * which you can use to read the contents of the map file as a String, and then parse it into a {@link GameMap}.
     */
    private final NativeFileChooser fileChooser;
    
    /**
     * The map. This is where all the game objects are stored.
     * This is owned by {@link BomberQuestGame} and not by {@link GameScreen}
     * because the map should not be destroyed if we temporarily switch to another screen.
     */
    private GameMap map;
    private Player player;

    /**
     * Constructor for BomberQuestGame.
     *
     * @param fileChooser The file chooser for the game, typically used in desktop environment.
     */
    public BomberQuestGame(NativeFileChooser fileChooser) {
        this.fileChooser = fileChooser;
    }

    /**
     * Called when the game is created. Initializes the SpriteBatch and Skin.
     * During the class constructor, libGDX is not fully initialized yet.
     * Therefore this method serves as a second constructor for the game,
     * and we can use libGDX resources here.
     */
    @Override
    public void create() {
        this.spriteBatch = new SpriteBatch(); // Create SpriteBatch for rendering
        this.skin = new Skin(Gdx.files.internal("skin/craftacular/craftacular-ui.json")); // Load UI skin
        MusicTrack.BACKGROUND.play(); // Play some background music

        FileHandle hardcodedMapFile = Gdx.files.internal("maps/map-1.properties");
        this.map = new GameMap(this, hardcodedMapFile);
        this.font = skin.getFont("font");

        goToMenu();
    }

    /**
     * Switches to the menu screen.
     */
    public void goToMenu() {
        this.setScreen(new MenuScreen(this, font)); // Set the current screen to MenuScreen
    }

    /**
     * Switches to the game screen.
     */
    public void goToGame() {
        this.setScreen(new GameScreen(this)); // Set the current screen to GameScreen
    }

    /** Returns the skin for UI elements. */
    public Skin getSkin() {
        return skin;
    }

    /** Returns the main SpriteBatch for rendering. */
    public SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }
    
    /** Returns the current map, if there is one. */
    public GameMap getMap() {
        return map;
    }
    
    /**
     * Switches to the given screen and disposes of the previous screen.
     * @param screen the new screen
     */
    @Override
    public void setScreen(Screen screen) {
        Screen previousScreen = super.screen;
        super.setScreen(screen);
        if (previousScreen != null) {
            previousScreen.dispose();
        }
    }

    /** Cleans up resources when the game is disposed. */
    @Override
    public void dispose() {
        getScreen().hide(); // Hide the current screen
        getScreen().dispose(); // Dispose the current screen
        spriteBatch.dispose(); // Dispose the spriteBatch
        skin.dispose(); // Dispose the skin
    }

    /**
     * Loads a map file and transitions to the game screen with the loaded map.
     *
     * @param mapPath The path to the .properties file containing the map data.
     */
    public void loadMap(String mapPath) {
        try {
            // Use FileHandle to load the map file
            FileHandle fileHandle = Gdx.files.internal(mapPath);

            // Create the GameMap object with the required parameters
            this.map = new GameMap(this, fileHandle);

            // Transition to the GameScreen with the loaded map
            goToGame();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to load map: " + mapPath);
        }
    }



}
