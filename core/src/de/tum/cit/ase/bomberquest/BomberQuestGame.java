package de.tum.cit.ase.bomberquest;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import de.tum.cit.ase.bomberquest.ScreenStates.GameOverScreen;
import de.tum.cit.ase.bomberquest.ScreenStates.GameWonScreen;
import de.tum.cit.ase.bomberquest.ScreenStates.MenuScreen;
import de.tum.cit.ase.bomberquest.ScreenStates.ScreenState;
import de.tum.cit.ase.bomberquest.audio.MusicTrack;
import de.tum.cit.ase.bomberquest.bonusFeatures.Score;
import de.tum.cit.ase.bomberquest.map.GameMap;
import de.tum.cit.ase.bomberquest.objects.Player;
import de.tum.cit.ase.bomberquest.screens.*;
import games.spooky.gdx.nativefilechooser.NativeFileChooser;

/**
 * Main class for the BomberQuest game, responsible for initializing and managing game states, resources, and screen transitions.
 * Extends libGDX's Game class to leverage its game lifecycle management.
 */
public class BomberQuestGame extends Game {

    private SpriteBatch spriteBatch;
    private BitmapFont font;
    private Skin skin;
    private final NativeFileChooser fileChooser;
    private GameMap map;
    private Player player;
    private String selectedMap;
    private ScreenState currentScreenState;
    private ScreenState previousScreenState;
    private MusicTrack currentMusicTrack;
    private Hud hud;
    private Sound gameWonSound;
    private Sound gameOverSound;
    private Score score = new Score();

    /**
     * Constructs a new BomberQuestGame instance with the specified file chooser.
     *
     * @param fileChooser the NativeFileChooser used for selecting files within the game
     */
    public BomberQuestGame(NativeFileChooser fileChooser) {
        this.fileChooser = fileChooser;
        currentMusicTrack = null;
    }

    /**
     * Initializes the game by setting up rendering components, loading resources, and transitioning to the main menu.
     * This method is called once when the application is created.
     */
    @Override
    public void create() {
        this.spriteBatch = new SpriteBatch(); // Create SpriteBatch for rendering
        this.skin = new Skin(Gdx.files.internal("skin/craftacular/craftacular-ui.json")); // Load UI skin

        gameWonSound = Gdx.audio.newSound(Gdx.files.internal("audio/gameWon.mp3"));
        gameOverSound = Gdx.audio.newSound(Gdx.files.internal("assets/audio/death.mp3"));

        this.font = skin.getFont("font");

        // this serves as a fallback in case the map loaded is corrupted
        FileHandle hardcodedMapFile = Gdx.files.internal("maps/Map 1.properties");
        this.hud = new Hud(spriteBatch, font);
        this.map = new GameMap(this, hardcodedMapFile, hud, score);

        goToMenu();
    }

    /**
     * Transitions the game to the main menu screen.
     */
    public void goToMenu() {
        setScreenWithState(ScreenState.MENU);
    }

    /**
     * Transitions the game to the main gameplay screen.
     */
    public void goToGame() {
        setScreenWithState(ScreenState.GAME);
    }

    /**
     * Sets the active screen based on the provided {@code ScreenState}.
     * Handles the transition logic and ensures proper resource management during screen changes.
     *
     * @param newState the ScreenState to transition to
     */
    public void setScreenWithState(ScreenState newState) {
        // Record previous screen state:
        if (currentScreenState != null) {
            previousScreenState = currentScreenState;
        }

        // Update current screen state:
        currentScreenState = newState;

        System.out.println("Transitioning to: " + currentScreenState + ", Previous: " + previousScreenState);

        // Play appropriate music for the new state
        switch (newState) {
            case MENU -> {
                setScreen(new MenuScreen(this, font));
                playMusic(MusicTrack.BACKGROUND);
            }
            case GAME -> {
                setScreen(new GameScreen(this, score, savedTime));
                playMusic(MusicTrack.GAMEPLAY_MUSIC);
            }
            case GAME_OVER -> {
                setScreen(new GameOverScreen(this, font));
                currentMusicTrack.stop();
                gameOverSound.play();
            }
            case GAME_WON -> {
                setScreen(new GameWonScreen(this, font));
                gameWonSound.play();
                if (currentMusicTrack != null) {
                    currentMusicTrack.stop();
                }
            }
        }
    }

    /**
     * Plays the specified music track, handling the transition from any currently playing track.
     *
     * @param track the MusicTrack to be played
     */
    private void playMusic(MusicTrack track) {
        if (currentMusicTrack != track) {
            if (currentMusicTrack != null) {
                currentMusicTrack.stop();
            }

            if (track == MusicTrack.GAMEPLAY_MUSIC) {
                track.setLooping(false);
            }
            currentMusicTrack = track;
            track.play();
        }
    }

    /**
     * Transitions the game to the game over screen.
     */
    public void goToGameOver() {
        setScreenWithState(ScreenState.GAME_OVER);
    }

    /**
     * Restarts the current game by disposing of the existing game screen and reloading the selected map.
     * If no map is selected, an error is logged.
     */
    public void restartGame() {
        Screen currentScreen = getScreen();
        score.resetScore();
        resetTime();
        if (currentScreen instanceof GameScreen) {
            ((GameScreen) currentScreen).setGameOver(false);
            currentScreen.dispose();
        }

        if (selectedMap != null) {
            loadMap(selectedMap);
        } else {
            Gdx.app.error("BomberQuestGame", "No map selected to restart the game.");
        }
        currentMusicTrack.play();
    }

    /**
     * Transitions the game to the game won screen.
     */
    public void goToGameWon() {
        setScreenWithState(ScreenState.GAME_WON);
    }

    /**
     * Loads a game map from the specified path and transitions to the gameplay screen.
     * If the map fails to load, an error message is printed.
     *
     * @param mapPath the file path of the map to load
     */
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
            this.map = new GameMap(this, fileHandle, hud, score);
            goToGame();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to load map: " + mapPath);
        }
    }

    /**
     * Sets the active screen and ensures that the previous screen is properly disposed of to free resources.
     *
     * @param screen the new Screen to set as active
     */
    @Override
    public void setScreen(Screen screen) {
        Screen previousScreen = super.screen;
        super.setScreen(screen);
        if (previousScreen != null) {
            previousScreen.dispose();
        }
    }

    /**
     * Disposes of all game resources, including screens, rendering components, and skins.
     * This method is called when the application is closing.
     */
    @Override
    public void dispose() {
        getScreen().hide();
        getScreen().dispose();
        spriteBatch.dispose();
        skin.dispose();
    }

    /**
     * Navigates back to the previous screen state if available.
     * If no previous state exists, defaults to transitioning to the main menu.
     */
    public void goBack() {
        if (previousScreenState != null) {
            System.out.println("Going back to: " + previousScreenState + " from: " + currentScreenState);

            // Grab the previous state to go back to
            ScreenState targetState = previousScreenState;

            // Clear previousScreenState to avoid bouncing back and forth
            previousScreenState = null;

            // Set our screen to that previous state
            setScreenWithState(targetState);
        } else {
            System.out.println("No previous state found. Returning to MENU.");
            setScreenWithState(ScreenState.MENU);
        }
    }

    // Time management
    // This design was necessary to maintain a consistent time reference across multiple game instances.
    // The extensive game menu mechanics require synchronized timing between different screens, making it impractical
    // to encapsulate the time management within individual objects or separate classes.
    private float savedTime = 5 * 60f;


    public float getSavedTime() {
        return savedTime;
    }

    public void setSavedTime(float savedTime) {
        this.savedTime = savedTime;
    }

    public void resetTime(){
        savedTime = 5*60f;
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

    public Hud getHud() {
        return hud;
    }

    public void setSpriteBatch(SpriteBatch spriteBatch) {
        this.spriteBatch = spriteBatch;
    }

    public BitmapFont getFont() {
        return font;
    }

    public void setFont(BitmapFont font) {
        this.font = font;
    }

    public void setSkin(Skin skin) {
        this.skin = skin;
    }

    public void setMap(GameMap map) {
        this.map = map;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public String getSelectedMap() {
        return selectedMap;
    }

    public void setSelectedMap(String selectedMap) {
        this.selectedMap = selectedMap;
    }

    public ScreenState getCurrentScreenState() {
        return currentScreenState;
    }

    public void setCurrentScreenState(ScreenState currentScreenState) {
        this.currentScreenState = currentScreenState;
    }

    public ScreenState getPreviousScreenState() {
        return previousScreenState;
    }

    public void setPreviousScreenState(ScreenState previousScreenState) {
        this.previousScreenState = previousScreenState;
    }

    public MusicTrack getCurrentMusicTrack() {
        return currentMusicTrack;
    }

    public void setCurrentMusicTrack(MusicTrack currentMusicTrack) {
        this.currentMusicTrack = currentMusicTrack;
    }

    public void setHud(Hud hud) {
        this.hud = hud;
    }

    public Sound getGameWonSound() {
        return gameWonSound;
    }

    public void setGameWonSound(Sound gameWonSound) {
        this.gameWonSound = gameWonSound;
    }

    public NativeFileChooser getFileChooser() {
        return fileChooser;
    }

    public Score getScore() {
        return score;
    }

    public void setScore(Score score) {
        this.score = score;
    }
}
