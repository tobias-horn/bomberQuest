package de.tum.cit.ase.bomberquest;

import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import games.spooky.gdx.nativefilechooser.desktop.DesktopFileChooser;

/**
 * The DesktopLauncher class is the entry point for the desktop version of the Bomber Quest game.
 * It sets up the game window and launches the game using LibGDX framework.
 */
public class DesktopLauncher {
	public static void main(String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setTitle("Bomber Quest");


		// Graphics.DisplayMode displayMode = Lwjgl3ApplicationConfiguration.getDisplayMode();
		// config.setWindowedMode(
		//     Math.round(0.8f * displayMode.width),
		//     Math.round(0.8f * displayMode.height)
		// );


		config.setWindowedMode(1024, 768);

		config.useVsync(true);
		config.setForegroundFPS(60);

		new Lwjgl3Application(new BomberQuestGame(new DesktopFileChooser()), config);
	}
}


