package de.tum.cit.ase.bomberquest.ui;

import com.badlogic.gdx.Input;
import java.util.HashMap;
import java.util.Map;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class KeyBindings {
    public static final String MOVE_UP = "move_up";
    public static final String MOVE_DOWN = "move_down";
    public static final String MOVE_LEFT = "move_left";
    public static final String MOVE_RIGHT = "move_right";
    public static final String PLACE_BOMB = "place_bomb";
    public static final String PAUSE_GAME = "pause_game";

    private static final Preferences prefs = Gdx.app.getPreferences("KeyBindings");

    private static final Map<String, Integer> bindings = new HashMap<>();

    static {
        bindings.put(MOVE_UP, Input.Keys.W);       // Default: W
        bindings.put(MOVE_DOWN, Input.Keys.S);     // Default: S
        bindings.put(MOVE_LEFT, Input.Keys.A);     // Default: A
        bindings.put(MOVE_RIGHT, Input.Keys.D);    // Default: D
        bindings.put(PLACE_BOMB, Input.Keys.SPACE); // Default: SPACE
        bindings.put(PAUSE_GAME, Input.Keys.ESCAPE); // Default: ESC
    }

    static {
        loadBindings();
    }

    public static String getActionName(String action) {
        switch (action) {
            case MOVE_UP:
                return "Move Up";
            case MOVE_DOWN:
                return "Move Down";
            case MOVE_LEFT:
                return "Move Left";
            case MOVE_RIGHT:
                return "Move Right";
            case PLACE_BOMB:
                return "Place Bomb";
            case PAUSE_GAME:
                return "Pause Game";
            default:
                return action;
        }
    }


    public static int getKey(String action) {
        return bindings.get(action);
    }

    public static void setKey(String action, int key) {
        bindings.put(action, key);
        saveBindings();
    }

    public static void saveBindings() {
        for (String action : bindings.keySet()) {
            prefs.putInteger(action, bindings.get(action));
        }
        prefs.flush();
    }

    public static void loadBindings() {
        for (String action : bindings.keySet()) {
            bindings.put(action, prefs.getInteger(action, bindings.get(action)));
        }
    }


}
