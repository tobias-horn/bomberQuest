package de.tum.cit.ase.bomberquest.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

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

    public void setPanelState(PanelState state) {
        switch (state) {
            case BLACK:
                panelTexture = blackPanelTexture;
                break;
            case RED:
                panelTexture = redPanelTexture;
                break;
            case BLUE:
                panelTexture = bluePanelTexture;
                break;
        }
    }

    public void render(String timerText) {
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        float scaleFactor = 0.3f;
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

        spriteBatch.end();
    }

    public float getScaledHeight() {
        float screenWidth = Gdx.graphics.getWidth();
        float scaleFactor = 0.3f;
        float newPanelWidth = screenWidth * scaleFactor;
        float aspectRatio = panelHeight / panelWidth;
        return newPanelWidth * aspectRatio;
    }

    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
    }

    public enum PanelState {
        BLACK,
        RED,
        BLUE
    }
}
