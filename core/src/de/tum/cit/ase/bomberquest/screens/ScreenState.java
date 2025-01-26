package de.tum.cit.ase.bomberquest.screens;

/**
 * ScreenState represents the various states or screens within the BomberQuest game.
 * It is used to manage transitions between different parts of the game such as the menu,
 * gameplay, pause screen, settings, file selection, game over, and game won screens.
 */
public enum ScreenState {
    /**
     * Represents the main menu screen where players can start the game, access settings, or quit.
     */
    MENU,

    /**
     * Represents the main gameplay screen where the game is actively played.
     */
    GAME,

    /**
     * Represents the pause screen, allowing players to pause the game and access pause-related options.
     */
    PAUSE,

    /**
     * Represents the settings screen where players can adjust game settings.
     */
    SETTINGS,

    /**
     * Represents the file selection screen, enabling players to select game maps or levels.
     */
    FILE_SELECTION,

    /**
     * Represents the game over screen, displayed when the player loses the game.
     */
    GAME_OVER,

    /**
     * Represents the game won screen, displayed when the player successfully completes the game.
     */
    GAME_WON
}
