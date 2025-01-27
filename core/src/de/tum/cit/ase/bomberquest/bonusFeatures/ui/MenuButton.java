package de.tum.cit.ase.bomberquest.bonusFeatures.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

/**
 * {@code MenuButton} is a custom button class for the BomberQuest UI, extending libGDX's {@link TextButton}.
 * It provides a styled button with customizable dimensions, font, and drawable states.
 * Additionally, it plays a click sound when the button is pressed.
 * <p>
 * Implemented according to the guidelines from the
 * <a href="https://libgdx.com/wiki/graphics/2d/ninepatches">libGDX NinePatch Wiki</a>.
 * </p>
 */
public class MenuButton extends TextButton {

    private float desiredWidth;
    private float desiredHeight;

    // Sound effect played on button click
    private static final Sound clickSound = Gdx.audio.newSound(Gdx.files.internal("assets/audio/buttonClick.mp3"));

    /**
     * Constructs a new {@code MenuButton} with the specified text, dimensions, font, and drawable styles.
     *
     * @param text          the text to display on the button
     * @param desiredWidth  the preferred width of the button
     * @param desiredHeight the preferred height of the button
     * @param font          the {@link BitmapFont} used for the button text
     * @param upDrawable    the {@link NinePatchDrawable} for the button's up (default) state
     * @param overDrawable  the {@link NinePatchDrawable} for the button's over (hovered) state
     */
    public MenuButton(String text,
                      float desiredWidth, float desiredHeight,
                      BitmapFont font,
                      NinePatchDrawable upDrawable,
                      NinePatchDrawable overDrawable) {

        super(text, createStyle(font, upDrawable, overDrawable));
        this.desiredWidth = desiredWidth;
        this.desiredHeight = desiredHeight;

        // Adds a click listener to play a sound when the button is clicked
        this.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clickSound.play();
                super.clicked(event, x, y);
            }
        });
    }

    /**
     * Returns the preferred width of the button.
     *
     * @return the desired width
     */
    @Override
    public float getPrefWidth() {
        return desiredWidth;
    }

    /**
     * Returns the preferred height of the button.
     *
     * @return the desired height
     */
    @Override
    public float getPrefHeight() {
        return desiredHeight;
    }

    /**
     * Creates a {@link TextButtonStyle} with the specified font and drawable states.
     *
     * @param font  the {@link BitmapFont} used for the button text
     * @param up    the {@link NinePatchDrawable} for the button's up (default) state
     * @param over  the {@link NinePatchDrawable} for the button's over (hovered) state
     * @return a configured {@code TextButtonStyle}
     */
    private static TextButtonStyle createStyle(BitmapFont font,
                                               NinePatchDrawable up,
                                               NinePatchDrawable over) {
        TextButtonStyle style = new TextButtonStyle();
        style.up = up;
        style.over = over;
        style.font = font;
        style.fontColor = Color.WHITE;
        return style;
    }
}
