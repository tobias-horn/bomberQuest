package de.tum.cit.ase.bomberquest.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

public class MenuButton extends TextButton {


    public MenuButton(String text,
                        float desiredWidth, float desiredHeight,
                        BitmapFont font,
                        NinePatchDrawable upDrawable,
                        NinePatchDrawable overDrawable) {

        super(text, createStyle(font, upDrawable, overDrawable));

        setSize(desiredWidth, desiredHeight);
    }

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

