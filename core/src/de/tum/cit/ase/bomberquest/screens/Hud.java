package de.tum.cit.ase.bomberquest.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import de.tum.cit.ase.bomberquest.map.ActivePowerUp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A Heads-Up Display (HUD) that displays information on the screen.
 * It uses a separate camera so that it is always fixed on the screen.
 */
public class Hud {

    private final SpriteBatch spriteBatch;
    private final BitmapFont font;
    private final OrthographicCamera camera;

    private Texture blackPanelTexture;
    private Texture redPanelTexture;
    private Texture bluePanelTexture;
    private Texture panelTexture;  // Current texture to draw

    private final float panelWidth;
    private final float panelHeight;

    // List of currently active power-ups to be shown in the HUD
    private final List<ActivePowerUp> activePowerUps = new ArrayList<>();


    public Hud(SpriteBatch spriteBatch, BitmapFont font) {
        this.spriteBatch = spriteBatch;
        this.font = font;
        this.camera = new OrthographicCamera();

        // Load all panel textures
        blackPanelTexture = new Texture(Gdx.files.internal("assets/menu/hudPanelBlack.png"));
        redPanelTexture = new Texture(Gdx.files.internal("assets/menu/hudPanelRed.png"));
        bluePanelTexture = new Texture(Gdx.files.internal("assets/menu/hudPanelBlue.png"));

        // Initialize with the normal (black) state
        panelTexture = blackPanelTexture;

        // Use one of the textures to get dimensions
        panelWidth = panelTexture.getWidth();
        panelHeight = panelTexture.getHeight();
    }


    /**
     * Call this once per frame (in your main game loop) to reduce
     * each power-up’s time by deltaTime and remove any expired ones.
     */
    public void update(float deltaTime) {
        Iterator<ActivePowerUp> itr = activePowerUps.iterator();
        while (itr.hasNext()) {
            ActivePowerUp pu = itr.next();
            pu.timeRemaining -= deltaTime;
            if (pu.timeRemaining <= 0) {
                itr.remove();
            }
        }
    }

    /**
     * Renders the HUD elements: the panel with text, and any active power-ups.
     * @param timerText A string to show on the panel (e.g. countdown or score).
     */
    public void render(String timerText) {
        // Set camera
        spriteBatch.setProjectionMatrix(camera.combined);

        // Begin drawing
        spriteBatch.begin();

        float screenWidth  = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();


        float scaleFactor = 0.3f;
        float newPanelWidth = screenWidth * scaleFactor;
        float aspectRatio = panelHeight / panelWidth;
        float newPanelHeight = newPanelWidth * aspectRatio;

        float panelX = (screenWidth - newPanelWidth) / 2f;
        float panelY = screenHeight - newPanelHeight;

        // Draw the panel texture
        spriteBatch.draw(panelTexture, panelX, panelY, newPanelWidth, newPanelHeight);

        // Draw the timer text in the middle of the panel
        GlyphLayout layout = new GlyphLayout(font, timerText);
        float textWidth = layout.width;
        float textHeight = layout.height;

        float textX = panelX + (newPanelWidth  - textWidth) / 2f;
        float textY = panelY + (newPanelHeight + textHeight) / 2f;

        font.draw(spriteBatch, layout, textX, textY);

        drawActivePowerUps();

        // End drawing
        spriteBatch.end();
    }

    /**
     * Optional convenience method: returns the scaled height of the HUD panel.
     */
    public float getScaledHeight() {
        float screenWidth  = Gdx.graphics.getWidth();
        float scaleFactor  = 0.3f;
        float newPanelWidth = screenWidth * scaleFactor;
        float aspectRatio  = panelHeight / panelWidth;
        return newPanelWidth * aspectRatio;
    }

    /**
     * Resizes the HUD camera (call when your game window is resized).
     */
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
    }

    /**
     * Adds a new power-up to display for 15s.
     * Typically called by the game code after a player picks up a power-up.
     */
    public void addActivePowerUp(ActivePowerUp powerUp) {
        activePowerUps.add(powerUp);
    }

    /**
     * For completeness, lets you directly set the panel's color state.
     */
    public void setPanelState(PanelState state) {
        switch (state) {
            case BLACK -> panelTexture = blackPanelTexture;
            case RED   -> panelTexture = redPanelTexture;
            case BLUE  -> panelTexture = bluePanelTexture;
        }
    }



    /**
     * Draws the active power-up icons in the top-right corner, side-by-side.
     */
    private void drawActivePowerUps() {
        float iconSize = 64f;
        float margin   = 10f;
        float spacing  = 5f;


        float xPos = Gdx.graphics.getWidth()  - margin - iconSize;
        float yPos = Gdx.graphics.getHeight() - margin - iconSize;

        for (ActivePowerUp pu : activePowerUps) {

            spriteBatch.draw(pu.icon, xPos, yPos, iconSize, iconSize);


            xPos -= (iconSize + spacing);
        }
    }

    // ------------------------------------------------------------------------
    // Enums and other data
    // ------------------------------------------------------------------------

    public enum PanelState {
        BLACK,
        RED,
        BLUE
    }



}
