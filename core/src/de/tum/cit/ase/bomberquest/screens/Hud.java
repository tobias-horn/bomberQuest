package de.tum.cit.ase.bomberquest.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import de.tum.cit.ase.bomberquest.textures.Textures;

/**
 * Hud manages the Heads-Up Display (HUD) for the BomberQuest game.
 * It displays essential game information such as the timer, concurrent bombs, blast radius, and remaining enemies.
 * The HUD is rendered using SpriteBatch and BitmapFont, and adapts to screen resizing.
 */
public class Hud {

    private final SpriteBatch spriteBatch;
    private final BitmapFont font;
    private final OrthographicCamera camera;

    private Texture blackPanelTexture;
    private Texture redPanelTexture;
    private Texture bluePanelTexture;
    private Texture panelTexture;

    private Texture transparentBlackTexture; // For underlay

    private final float panelWidth;
    private final float panelHeight;

    private int concurrentBombCount = 1;
    private int blastRadiusCount = 1;
    private int remainingEnemiesCount = 0;


    private int timerInSeconds;
    private int score;

    /**
     * Constructs the Hud with the specified SpriteBatch and BitmapFont.
     * Initializes textures and sets up the HUD panel.
     *
     * @param spriteBatch The SpriteBatch used for rendering HUD elements.
     * @param font        The BitmapFont used for rendering text on the HUD.
     */
    public Hud(SpriteBatch spriteBatch, BitmapFont font) {
        this.spriteBatch = spriteBatch;
        this.font = font;
        this.camera = new OrthographicCamera();

        blackPanelTexture = new Texture(Gdx.files.internal("assets/menu/hudPanelBlack.png"));
        redPanelTexture = new Texture(Gdx.files.internal("assets/menu/hudPanelRed.png"));
        bluePanelTexture = new Texture(Gdx.files.internal("assets/menu/hudPanelBlue.png"));

        panelTexture = blackPanelTexture;

        panelWidth = panelTexture.getWidth();
        panelHeight = panelTexture.getHeight();

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0.3f); // Black with 0.3 opacity
        pixmap.fill();
        transparentBlackTexture = new Texture(pixmap);
        pixmap.dispose();
    }

    /**
     * Updates the counts for concurrent bombs, blast radius, and remaining enemies.
     * This method should be called each frame to reflect the current game state.
     *
     * @param concurrentBombs The current number of bombs placed by the player.
     * @param blastRadius     The current blast radius of the bombs.
     * @param enemies         The number of enemies remaining on the map.
     */
    public void setCounts(int concurrentBombs, int blastRadius, int enemies) {
        this.concurrentBombCount = concurrentBombs;
        this.blastRadiusCount = blastRadius;
        this.remainingEnemiesCount = enemies;
    }

    /**
     * Renders the HUD panel and associated information such as the timer,
     * concurrent bombs, blast radius, remaining enemies, and score.
     *
     * @param timerText The formatted timer text to display.
     */
    public void render(String timerText) {

        spriteBatch.setProjectionMatrix(camera.combined);

        spriteBatch.begin();

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        float scaleFactor = 0.4f;
        float newPanelWidth = screenWidth * scaleFactor;
        float aspectRatio = panelHeight / panelWidth;
        float newPanelHeight = newPanelWidth * aspectRatio;

        float panelX = (screenWidth - newPanelWidth) / 2f;
        float panelY = screenHeight - newPanelHeight;

        spriteBatch.draw(panelTexture, panelX, panelY, newPanelWidth, newPanelHeight);

        GlyphLayout layout = new GlyphLayout(font, timerText);
        float textWidth = layout.width;
        float textHeight = layout.height;

        float textX = panelX + (newPanelWidth - textWidth) / 2f;
        float textY = panelY + (newPanelHeight + textHeight) / 2f;
        font.draw(spriteBatch, layout, textX, textY);

        float iconSize = 45f;
        float margin = 15f;
        float padding = 10f;

        // Render Blast Radius Count
        GlyphLayout blastLayout = new GlyphLayout(font, String.valueOf(blastRadiusCount));
        float blastTextWidth = blastLayout.width;
        float blastTextHeight = blastLayout.height;

        float leftOverlayWidth = iconSize + 5 + blastTextWidth + 2 * padding;
        float leftOverlayHeight = iconSize + 2 * padding;

        float leftOverlayX = margin;
        float leftOverlayY = screenHeight - margin - leftOverlayHeight;

        spriteBatch.draw(transparentBlackTexture, leftOverlayX, leftOverlayY, leftOverlayWidth, leftOverlayHeight);

        float leftIconX = leftOverlayX + padding;
        float leftIconY = leftOverlayY + padding;
        spriteBatch.draw(Textures.BLASTRADIOUS_HUD, leftIconX, leftIconY, iconSize, iconSize);

        float blastTextX = leftIconX + iconSize + 5;
        float blastTextY = leftIconY + (iconSize + blastTextHeight) / 2f;
        font.draw(spriteBatch, blastLayout, blastTextX, blastTextY);

        // Render Remaining Enemies Count
        GlyphLayout enemiesLayout = new GlyphLayout(font, String.valueOf(remainingEnemiesCount));
        float enemiesTextWidth = enemiesLayout.width;
        float enemiesTextHeight = enemiesLayout.height;

        float enemiesOverlayWidth = iconSize + 5 + enemiesTextWidth + 2 * padding;
        float enemiesOverlayHeight = iconSize + 2 * padding;

        // Position the overlay on the screen (e.g., bottom-left)
        float enemiesOverlayX = margin;
        float enemiesOverlayY = margin;

        // Draw the overlay background
        spriteBatch.draw(transparentBlackTexture, enemiesOverlayX, enemiesOverlayY, enemiesOverlayWidth, enemiesOverlayHeight);

        // Draw the icon for remaining enemies
        float enemiesIconX = enemiesOverlayX + padding;
        float enemiesIconY = enemiesOverlayY + padding;
        spriteBatch.draw(Textures.ENEMYCOUNT_HUD, enemiesIconX, enemiesIconY, iconSize, iconSize);

        // Draw the remaining enemies count
        float enemiesTextX = enemiesIconX + iconSize + 5;
        float enemiesTextY = enemiesIconY + (iconSize + enemiesTextHeight) / 2f;
        font.draw(spriteBatch, enemiesLayout, enemiesTextX, enemiesTextY);

        // Render Concurrent Bomb Count
        GlyphLayout bombLayout = new GlyphLayout(font, String.valueOf(concurrentBombCount));
        float bombTextWidth = bombLayout.width;
        float bombTextHeight = bombLayout.height;

        float rightOverlayWidth = iconSize + 5 + bombTextWidth + 2 * padding;
        float rightOverlayHeight = iconSize + 2 * padding;

        float rightOverlayX = screenWidth - margin - rightOverlayWidth;
        float rightOverlayY = screenHeight - margin - rightOverlayHeight;

        spriteBatch.draw(transparentBlackTexture, rightOverlayX, rightOverlayY, rightOverlayWidth, rightOverlayHeight);

        float rightIconX = rightOverlayX + padding;
        float rightIconY = rightOverlayY + padding;
        spriteBatch.draw(Textures.CONCURRENTBOMB_HUD, rightIconX, rightIconY, iconSize, iconSize);

        float bombTextX = rightIconX + iconSize + 5;
        float bombTextY = rightIconY + (iconSize + bombTextHeight) / 2f;
        font.draw(spriteBatch, bombLayout, bombTextX, bombTextY);

        // Render Score in bottom-right corner
        String scoreText = "Score: " + score;
        GlyphLayout scoreLayout = new GlyphLayout(font, scoreText);
        float scoreTextWidth = scoreLayout.width;
        float scoreTextHeight = scoreLayout.height;

        float scoreOverlayWidth = scoreTextWidth + 2 * padding;
        float scoreOverlayHeight = iconSize + 2 * padding;

        float scoreOverlayX = screenWidth - margin - scoreOverlayWidth;
        float scoreOverlayY = margin;

        spriteBatch.draw(transparentBlackTexture, scoreOverlayX, scoreOverlayY, scoreOverlayWidth, scoreOverlayHeight);

        float scoreTextX = scoreOverlayX + padding;
        float scoreTextY = scoreOverlayY + padding + (iconSize + scoreTextHeight) / 2f;

        font.draw(spriteBatch, scoreLayout, scoreTextX, scoreTextY);

        spriteBatch.end();
    }

    /**
     * Returns the scaled height of the HUD panel based on the screen width and aspect ratio.
     *
     * @return The scaled height of the HUD panel.
     */
    public float getScaledHeight() {
        float screenWidth = Gdx.graphics.getWidth();
        float scaleFactor = 0.3f;
        float newPanelWidth = screenWidth * scaleFactor;
        float aspectRatio = panelHeight / panelWidth;
        return newPanelWidth * aspectRatio;
    }

    /**
     * Handles resizing of the HUD by updating the camera's orthographic projection.
     *
     * @param width  The new width of the screen.
     * @param height The new height of the screen.
     */
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
    }

    /**
     * Sets the current state of the HUD panel, changing its appearance based on the specified state.
     *
     * @param state The new {@code PanelState} to apply to the HUD panel.
     */
    public void setPanelState(PanelState state) {
        switch (state) {
            case BLACK -> panelTexture = blackPanelTexture;
            case RED -> panelTexture = redPanelTexture;
            case BLUE -> panelTexture = bluePanelTexture;
        }
    }

    /**
     * Enum representing the different visual states of the HUD panel.
     */
    public enum PanelState {
        BLACK,
        RED,
        BLUE
    }

    /**
     * Updates the count of remaining enemies displayed on the HUD.
     *
     * @param count The number of enemies remaining.
     */
    public void setRemainingEnemiesCount(int count) {
        this.remainingEnemiesCount = count;
    }

    /**
     * Sets the remaining time in seconds on the HUD.
     * @param seconds The remaining time in seconds.
     */
    public void setTimerInSeconds(int seconds) {
        this.timerInSeconds = seconds;
    }

    /**
     * Returns the remaining time in seconds as displayed on the HUD.
     * @return The remaining time in seconds.
     */
    public int getTimerInSeconds() {
        return timerInSeconds;
    }

    /**
     * Sets the current score to display on the HUD.
     * @param score The player's current score.
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Returns the current score displayed on the HUD.
     * @return The current score.
     */
    public int getScore() {
        return score;
    }
}
