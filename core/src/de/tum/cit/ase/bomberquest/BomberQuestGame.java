package de.tum.cit.ase.bomberquest;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import de.tum.cit.ase.bomberquest.audio.MusicTrack;
import de.tum.cit.ase.bomberquest.map.GameMap;
import de.tum.cit.ase.bomberquest.objects.Player;
import de.tum.cit.ase.bomberquest.screens.*;
import games.spooky.gdx.nativefilechooser.NativeFileChooser;

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

    public BomberQuestGame(NativeFileChooser fileChooser) {
        this.fileChooser = fileChooser;
        currentMusicTrack = null;
    }


    @Override
    public void create() {
        this.spriteBatch = new SpriteBatch(); // Create SpriteBatch for rendering
        this.skin = new Skin(Gdx.files.internal("skin/craftacular/craftacular-ui.json")); // Load UI skin


        if (MusicTrack.BACKGROUND != null) {
            // MusicTrack.BACKGROUND.play();
        }

        gameWonSound = Gdx.audio.newSound(Gdx.files.internal("audio/gameWon.mp3"));

        this.font = skin.getFont("font");

        FileHandle hardcodedMapFile = Gdx.files.internal("maps/map-1.properties");
        this.hud = new Hud(spriteBatch, font);
        this.map = new GameMap(this, hardcodedMapFile, hud);



        goToMenu();
    }


    public void goToMenu() {
        setScreenWithState(ScreenState.MENU);
    }

    public void goToGame() {
        setScreenWithState(ScreenState.GAME);
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


            this.map = new GameMap(this, fileHandle, hud);


            goToGame();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to load map: " + mapPath);
        }
    }


    public void goToGameOver() {
        setScreenWithState(ScreenState.GAME_OVER);
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

    public void goToGameWon() {
        setScreenWithState(ScreenState.GAME_WON);
    }

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
                setScreen(new GameScreen(this));
                playMusic(MusicTrack.TWO_MINUTE_TRACK);
            }
            case GAME_OVER -> {
                setScreen(new GameOverScreen(this, font));
                playMusic(MusicTrack.BACKGROUND);
            }
            case GAME_WON -> {
                setScreen(new GameWonScreen(this, font));
                gameWonSound.play();
                currentMusicTrack.stop();
            }
        }
    }

    private void playMusic(MusicTrack track) {
        if (currentMusicTrack != track) {
            // Stop the current track if it's playing
            if (currentMusicTrack != null) {
                currentMusicTrack.stop();
            }

            // Set the two-minute track's specific behavior
            if (track == MusicTrack.TWO_MINUTE_TRACK) {
                track.setLooping(false); // Ensure the track doesn't loop
            }

            // Play the new track
            track.play();
            currentMusicTrack = track; // Update the reference
        }
    }

    public void goBack() {
        if (previousScreenState != null) {
            System.out.println("Going back to: " + previousScreenState + " from: " + currentScreenState);

            // Grab the previous state to go back to
            ScreenState targetState = previousScreenState;

            // Clear previousScreenState to avoid bouncing back and forth
            previousScreenState = null;

            // Now set our screen to that previous state
            setScreenWithState(targetState);
        } else {
            System.out.println("No previous state found. Returning to MENU.");
            // If we have no recorded previous screen, let's default to the menu
            setScreenWithState(ScreenState.MENU);
        }
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
}
