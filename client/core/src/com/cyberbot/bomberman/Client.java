package com.cyberbot.bomberman;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.cyberbot.bomberman.managers.GameScreenManager;

public class Client extends Game {

    public static final float V_WIDTH = 100;
    public static final float V_HEIGHT = 100;

    private GameScreenManager gsm;

    @Override
    public void create() {
        final float w = Gdx.graphics.getWidth();
        final float h = Gdx.graphics.getHeight();

        gsm = new GameScreenManager(this);
    }
}
