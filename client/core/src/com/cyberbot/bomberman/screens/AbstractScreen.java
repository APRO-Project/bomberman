package com.cyberbot.bomberman.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;

public abstract class AbstractScreen implements Screen {

    public abstract void update(float delta);

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(0.25f, 0.25f, 0.25f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }
}
