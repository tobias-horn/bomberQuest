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
    
    /** The SpriteBatch used to draw the HUD. This is the same as the one used in the GameScreen. */
    private final SpriteBatch spriteBatch;
    /** The font used to draw text on the screen. */
    private final BitmapFont font;
    /** The camera used to render the HUD. */
    private final OrthographicCamera camera;

    private final Texture panelTexture;

    private final float panelWidth;
    private final float panelHeight;
    
    public Hud(SpriteBatch spriteBatch, BitmapFont font) {
        this.spriteBatch = spriteBatch;
        this.font = font;
        this.camera = new OrthographicCamera();
        panelTexture = new Texture(Gdx.files.internal("assets/menu/hudPanel.png"));
        panelWidth = panelTexture.getWidth();
        panelHeight = panelTexture.getHeight();
    }
    
    /**
     * Renders the HUD on the screen.
     * This uses a different OrthographicCamera so that the HUD is always fixed on the screen.
     */
    public void render(String timer) {
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


        GlyphLayout layout = new GlyphLayout(font, timer);


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
    
    /**
     * Resizes the HUD when the screen size changes.
     * This is called when the window is resized.
     * @param width The new width of the screen.
     * @param height The new height of the screen.
     */
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
    }
    
}
