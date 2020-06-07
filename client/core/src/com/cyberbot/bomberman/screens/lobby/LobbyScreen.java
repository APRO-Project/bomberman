package com.cyberbot.bomberman.screens.lobby;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cyberbot.bomberman.core.models.net.packets.Lobby;
import com.cyberbot.bomberman.screens.AbstractScreen;

public class LobbyScreen extends AbstractScreen {
    private final OrthographicCamera camera;
    private final SpriteBatch batch;
    private final Viewport viewport;

    private final LobbyLayout lobbyLayout;

    public LobbyScreen(LobbyInteraction delegate) {

        batch = new SpriteBatch();
        camera = new OrthographicCamera();

        camera.setToOrtho(false, 1920, 1080);
        viewport = new FitViewport(1920, 1080);

        lobbyLayout = new LobbyLayout(viewport, delegate);
    }

    public void updateLobby(Lobby lobby, boolean isOwner) {
        lobbyLayout.updateLobby(lobby, isOwner);
    }

    @Override
    public void update(float delta) {
        camera.update();

        batch.setProjectionMatrix(camera.combined);
        lobbyLayout.act(delta);
    }

    @Override
    public void showError(String msg) {
        lobbyLayout.showError(msg);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(lobbyLayout);
        lobbyLayout.createLobbyUi();
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
        lobbyLayout.clear();
    }

    @Override
    public void dispose() {
        lobbyLayout.dispose();
        batch.dispose();
    }
}
