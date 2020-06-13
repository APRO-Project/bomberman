package com.cyberbot.bomberman.screens.login;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cyberbot.bomberman.screens.AbstractScreen;

public class LoginScreen extends AbstractScreen {

    private final OrthographicCamera camera;
    private final SpriteBatch batch;
    private final Viewport viewport;

    private final LoginLayout loginLayout;

    public LoginScreen(LoginInteraction delegate) {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();

        camera.setToOrtho(false, 384, 216);
        viewport = new FitViewport(384, 216);

        loginLayout = new LoginLayout(viewport, delegate);
    }

    public void showError(String msg) {
        loginLayout.showError(msg);
    }

    @Override
    public void update(float delta) {
        camera.update();

        batch.setProjectionMatrix(camera.combined);
        loginLayout.act(delta);
    }

    @Override
    public void show() {
        loginLayout.createLoginLayout();
        Gdx.input.setInputProcessor(loginLayout);
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        batch.begin();
        batch.end();
        loginLayout.draw();

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
        loginLayout.clear();
    }

    @Override
    public void dispose() {
        loginLayout.dispose();
        batch.dispose();
    }
}
