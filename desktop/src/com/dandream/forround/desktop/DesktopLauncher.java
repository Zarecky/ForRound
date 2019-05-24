package com.dandream.forround.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.dandream.forround.StartGame;
import com.dandream.forround.utils.GameCallback;

public class DesktopLauncher {

	public static void main(String[] args) {
		new DesktopLauncher();
	}

	public DesktopLauncher() {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.height = 800;
		config.width = 480;
		new LwjglApplication(new StartGame(callback), config);
	}

	private GameCallback callback = new GameCallback() {
		@Override
		public void sendMessage(int message) {

		}
	};
}
