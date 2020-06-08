package com.cyberbot.bomberman;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.cyberbot.bomberman.controllers.ScreenController;

public class Client extends Game {

    public static final float V_WIDTH = 100;
    public static final float V_HEIGHT = 100;

    private ScreenController gsm;

    @Override
    public void create() {
        final float w = Gdx.graphics.getWidth();
        final float h = Gdx.graphics.getHeight();

        gsm = new ScreenController(this);
    }

    public ScreenController getGsm() {
        return gsm;
    }
}
