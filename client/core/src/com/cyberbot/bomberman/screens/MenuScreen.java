package com.cyberbot.bomberman.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cyberbot.bomberman.Client;
import com.cyberbot.bomberman.controllers.InputController;
import com.cyberbot.bomberman.screens.menu.MenuOptions;

public class MenuScreen extends AbstractScreen {

    private final Texture background;

    OrthographicCamera camera;
    SpriteBatch batch;
    Viewport viewport;


    MenuOptions menuOptions;

    public MenuScreen(final Client app) {
        super(app);

        batch = new SpriteBatch();
        camera = new OrthographicCamera();

        camera.setToOrtho(false, 1920, 1080);
        viewport = new FitViewport(1920, 1080);
        background = new Texture("./textures/menu_bg.png");

        menuOptions = new MenuOptions(viewport);
        menuOptions.createMenuOptions();
        Gdx.input.setInputProcessor(menuOptions);
    }

    @Override
    public void update(float delta) {
        camera.update();

        batch.setProjectionMatrix(camera.combined);
        menuOptions.act(delta);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        super.render(delta);

        batch.begin();
        batch.draw(background, 0 ,0,1920f,1080f);
        batch.end();
        menuOptions.draw();

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
        menuOptions.dispose();
        batch.dispose();
    }
}
