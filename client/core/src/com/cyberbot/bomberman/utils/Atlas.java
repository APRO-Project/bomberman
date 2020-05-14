package com.cyberbot.bomberman.utils;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class Atlas {
    private static TextureAtlas instance = null;

    public static TextureAtlas getInstance() {
        if (instance == null) {
            instance = new TextureAtlas("./textures/bomberman.atlas");
        }

        return instance;
    }
}
