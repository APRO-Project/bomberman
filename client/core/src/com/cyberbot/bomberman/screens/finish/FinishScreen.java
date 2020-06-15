package com.cyberbot.bomberman.screens.finish;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cyberbot.bomberman.screens.AbstractScreen;

import java.util.List;

public class FinishScreen extends AbstractScreen {
    private final OrthographicCamera camera;
    private final SpriteBatch batch;
    private final Viewport viewport;

    private final FinishLayout finishLayout;

    public FinishScreen(FinishInteraction delegate) {

        batch = new SpriteBatch();
        camera = new OrthographicCamera();

        camera.setToOrtho(false, 384, 216);
        viewport = new FitViewport(384, 216);

        finishLayout = new FinishLayout(viewport, delegate);
    }

    public void updateFinish(List<String> table) {
        finishLayout.updateScoreTable(table);
    }

    @Override
    public void update(float delta) {
        camera.update();

        batch.setProjectionMatrix(camera.combined);
        finishLayout.act(delta);
    }

    @Override
    public void showError(String msg) {
        finishLayout.showError(msg);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(finishLayout);
        finishLayout.createFinishUi();
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        batch.begin();
        batch.end();
        finishLayout.draw();
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
        finishLayout.clear();
    }

    @Override
    public void dispose() {
        finishLayout.dispose();
        batch.dispose();
    }
}
