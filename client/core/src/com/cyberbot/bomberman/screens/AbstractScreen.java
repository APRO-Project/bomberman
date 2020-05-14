package com.cyberbot.bomberman.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.cyberbot.bomberman.Client;

public abstract class AbstractScreen implements Screen {

    protected final Client app;

    public AbstractScreen(final Client app) {
        this.app = app;
    }

    public abstract void update(float delta);

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(0.25F, 0.25F, 0.25F, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }
}
