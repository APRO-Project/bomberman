package com.cyberbot.bomberman.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cyberbot.bomberman.Client;
import com.cyberbot.bomberman.controllers.GameScreenController;
import com.cyberbot.bomberman.screens.lobby.LobbyLayout;

public class LobbyScreen extends AbstractScreen{

    final OrthographicCamera camera;
    final SpriteBatch batch;
    final Viewport viewport;
    private boolean isOwner;

    LobbyLayout lobbyLayout;

    public LobbyScreen(Client app, GameScreenController gameScreenController, String playerName, boolean isOwner) {
        super(app, gameScreenController);
        this.isOwner = isOwner;

        batch = new SpriteBatch();
        camera = new OrthographicCamera();

        camera.setToOrtho(false, 1920, 1080);
        viewport = new FitViewport(1920, 1080);

        lobbyLayout = new LobbyLayout(viewport, false, gameScreenController);
        lobbyLayout.createLobbyUi();
        Gdx.input.setInputProcessor(lobbyLayout);

        lobbyLayout.addPlayer(playerName);
    }

    public LobbyScreen(Client app, boolean isOwner, GameScreenController gameScreenController) {
        super(app, gameScreenController);
        this.isOwner = isOwner;

        batch = new SpriteBatch();
        camera = new OrthographicCamera();

        camera.setToOrtho(false, 1920, 1080);
        viewport = new FitViewport(1920, 1080);

        lobbyLayout = new LobbyLayout(viewport, false, gameScreenController);
        lobbyLayout.createLobbyUi();
        Gdx.input.setInputProcessor(lobbyLayout);
    }

    public void addPlayer(String nick){
        lobbyLayout.addPlayer(nick);
    }

    public void addPlayer(String nick, int number){
        lobbyLayout.addPlayer(nick, number);
    }

    @Override
    public void update(float delta) {
        camera.update();

        batch.setProjectionMatrix(camera.combined);
        lobbyLayout.act(delta);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        super.render(delta);

        batch.begin();
        batch.end();
        lobbyLayout.draw();
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
        lobbyLayout.dispose();
        batch.dispose();
    }
}
