package com.cyberbot.bomberman.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.cyberbot.bomberman.Client;
import com.cyberbot.bomberman.controllers.GameScreenController;

public abstract class AbstractScreen implements Screen {

    protected final Client app;
    protected final GameScreenController gameScreenController;

    public AbstractScreen(final Client app, GameScreenController gameScreenController) {
        this.app = app;
        this.gameScreenController = gameScreenController;
    }

    public abstract void update(float delta);

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(0.25f, 0.25f, 0.25f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }
}
