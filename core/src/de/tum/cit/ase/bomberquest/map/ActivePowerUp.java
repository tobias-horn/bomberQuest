package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class ActivePowerUp {
    public TextureRegion icon;
    public float timeRemaining;

    public ActivePowerUp(TextureRegion icon, float duration) {
        this.icon = icon;
        this.timeRemaining = duration;
    }
}
