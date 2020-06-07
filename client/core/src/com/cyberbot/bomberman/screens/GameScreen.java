package com.cyberbot.bomberman.screens;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cyberbot.bomberman.controllers.NetworkedGameplayController;
import com.cyberbot.bomberman.core.models.net.data.PlayerData;
import com.cyberbot.bomberman.core.models.tiles.MissingLayersException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.SocketAddress;

import static com.cyberbot.bomberman.core.utils.Constants.PPM;

public class GameScreen extends AbstractScreen {
    private final static int VIEWPORT_WIDTH = 15;
    private final static int VIEWPORT_HEIGHT = 15;

    private final OrthographicCamera camera;
    private final Viewport viewport;

    private final Box2DDebugRenderer b2dr;

    private final SpriteBatch batch;

    private final NetworkedGameplayController gameplayController;

    public GameScreen(final PlayerData playerData, final String mapPath, final SocketAddress serverAddress)
        throws IOException, MissingLayersException, ParserConfigurationException, SAXException {

        camera = new OrthographicCamera();
        camera.setToOrtho(false, VIEWPORT_WIDTH * PPM, VIEWPORT_HEIGHT * PPM);
        viewport = new FitViewport(VIEWPORT_WIDTH * PPM, VIEWPORT_HEIGHT * PPM);

        b2dr = new Box2DDebugRenderer();
        batch = new SpriteBatch();

        gameplayController = new NetworkedGameplayController(playerData, mapPath, serverAddress);
    }

    @Override
    public void show() {

    }

    @Override
    public void update(float delta) {
        gameplayController.update(delta);

        camera.update();
        batch.setProjectionMatrix(camera.combined);
    }

    @Override
    public void showError(String msg) {

    }

    @Override
    public void render(float delta) {
        super.render(delta);

        batch.begin();
        gameplayController.draw(batch);
        batch.end();

        b2dr.render(gameplayController.getWorld(), camera.combined.cpy().scl(PPM));
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
        gameplayController.dispose();
        b2dr.dispose();
    }
}
