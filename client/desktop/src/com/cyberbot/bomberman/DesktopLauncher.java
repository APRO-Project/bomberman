package com.cyberbot.bomberman;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.height = 960;
        config.width = 960;
        config.samples = 4;
        config.addIcon("Icon.png", Files.FileType.Internal);
        config.title = "Boomerman";
        new LwjglApplication(new Client(), config);
    }
}
