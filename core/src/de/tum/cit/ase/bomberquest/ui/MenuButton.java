package de.tum.cit.ase.bomberquest.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

public class MenuButton extends TextButton {

    private float desiredWidth;
    private float desiredHeight;


    private static final Sound clickSound = Gdx.audio.newSound(Gdx.files.internal("assets/audio/buttonClick.mp3"));

    public MenuButton(String text,
                      float desiredWidth, float desiredHeight,
                      BitmapFont font,
                      NinePatchDrawable upDrawable,
                      NinePatchDrawable overDrawable) {

        super(text, createStyle(font, upDrawable, overDrawable));
        this.desiredWidth = desiredWidth;
        this.desiredHeight = desiredHeight;


        this.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clickSound.play();
                super.clicked(event, x, y);
            }
        });
    }

    @Override
    public float getPrefWidth() {
        return desiredWidth;
    }

    @Override
    public float getPrefHeight() {
        return desiredHeight;
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
