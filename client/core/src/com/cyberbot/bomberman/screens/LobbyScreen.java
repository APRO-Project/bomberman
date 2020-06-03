package com.cyberbot.bomberman.screens;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cyberbot.bomberman.Client;
import com.cyberbot.bomberman.controllers.GameScreenController;

public class LobbyScreen extends AbstractScreen{

    final OrthographicCamera camera;
    final SpriteBatch batch;
    final Viewport viewport;

    public LobbyScreen(Client app, GameScreenController gameScreenController) {
        super(app, gameScreenController);

        batch = new SpriteBatch();
        camera = new OrthographicCamera();

        camera.setToOrtho(false, 1920, 1080);
        viewport = new FitViewport(1920, 1080);
    }

    @Override
    public void update(float delta) {
        camera.update();

        batch.setProjectionMatrix(camera.combined);

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        super.render(delta);

        batch.begin();
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
