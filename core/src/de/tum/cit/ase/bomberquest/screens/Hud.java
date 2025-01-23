package de.tum.cit.ase.bomberquest.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import de.tum.cit.ase.bomberquest.textures.Textures;

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

    public Hud(SpriteBatch spriteBatch, BitmapFont font) {
        this.spriteBatch = spriteBatch;
        this.font = font;
        this.camera = new OrthographicCamera();

        blackPanelTexture = new Texture(Gdx.files.internal("assets/menu/hudPanelBlack.png"));
        redPanelTexture   = new Texture(Gdx.files.internal("assets/menu/hudPanelRed.png"));
        bluePanelTexture  = new Texture(Gdx.files.internal("assets/menu/hudPanelBlue.png"));

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
     * Removed update(float deltaTime) that was subtracting power-up time.
     */

    /**
     * Instead, we call this each frame to set the current number of
     * concurrent bombs & blast radius from the game map.
     */
    public void setCounts(int concurrentBombs, int blastRadius, int enemies) {
        this.concurrentBombCount = concurrentBombs;
        this.blastRadiusCount = blastRadius;
        this.remainingEnemiesCount = enemies;
    }

    /**
     * Renders the HUD panel (center) plus the permanent power-up counts.
     */
    public void render(String timerText) {

        spriteBatch.setProjectionMatrix(camera.combined);

        spriteBatch.begin();

        float screenWidth  = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();


        float scaleFactor = 0.4f;
        float newPanelWidth = screenWidth * scaleFactor;
        float aspectRatio = panelHeight / panelWidth;
        float newPanelHeight = newPanelWidth * aspectRatio;

        float panelX = (screenWidth - newPanelWidth) / 2f;
        float panelY = screenHeight - newPanelHeight;

        spriteBatch.draw(panelTexture, panelX, panelY, newPanelWidth, newPanelHeight);

        GlyphLayout layout = new GlyphLayout(font, timerText);
        float textWidth  = layout.width;
        float textHeight = layout.height;

        float textX = panelX + (newPanelWidth  - textWidth) / 2f;
        float textY = panelY + (newPanelHeight + textHeight) / 2f;
        font.draw(spriteBatch, layout, textX, textY);

        float iconSize = 45f;
        float margin   = 15f;
        float padding  = 10f;


        GlyphLayout blastLayout = new GlyphLayout(font, String.valueOf(blastRadiusCount));
        float blastTextWidth  = blastLayout.width;
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

        // Render remaining enemies count
        GlyphLayout enemiesLayout = new GlyphLayout(font, String.valueOf(remainingEnemiesCount));
        float enemiesTextWidth = enemiesLayout.width;
        float enemiesTextHeight = enemiesLayout.height;

        float enemiesOverlayWidth = iconSize + 5 + enemiesTextWidth + 2 * padding;
        float enemiesOverlayHeight = iconSize + 2 * padding;

// Position the overlay on the screen (e.g., top-center)
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

        GlyphLayout bombLayout = new GlyphLayout(font, String.valueOf(concurrentBombCount));
        float bombTextWidth  = bombLayout.width;
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

        spriteBatch.end();
    }

    /**
     * Returns the scaled height of the HUD panel if needed by other code.
     */
    public float getScaledHeight() {
        float screenWidth  = Gdx.graphics.getWidth();
        float scaleFactor  = 0.3f;
        float newPanelWidth = screenWidth * scaleFactor;
        float aspectRatio  = panelHeight / panelWidth;
        return newPanelWidth * aspectRatio;
    }


    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
    }

    public void setPanelState(PanelState state) {
        switch (state) {
            case BLACK -> panelTexture = blackPanelTexture;
            case RED   -> panelTexture = redPanelTexture;
            case BLUE  -> panelTexture = bluePanelTexture;
        }
    }

    public enum PanelState {
        BLACK,
        RED,
        BLUE
    }

    public void setRemainingEnemiesCount(int count) {
        this.remainingEnemiesCount = count;
    }
}
