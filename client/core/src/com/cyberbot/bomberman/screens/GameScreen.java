package com.cyberbot.bomberman.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cyberbot.bomberman.Client;
import com.cyberbot.bomberman.controllers.ActionController;
import com.cyberbot.bomberman.controllers.GameStateController;
import com.cyberbot.bomberman.controllers.InputController;
import com.cyberbot.bomberman.controllers.TextureController;
import com.cyberbot.bomberman.models.KeyBinds;
import com.cyberbot.bomberman.models.defs.PlayerDef;
import com.cyberbot.bomberman.models.entities.PlayerEntity;
import com.cyberbot.bomberman.models.tiles.TileMap;

import java.util.Arrays;
import java.util.InvalidPropertiesFormatException;

import static com.cyberbot.bomberman.utils.Constants.PPM;

public class GameScreen extends AbstractScreen {
    private final static int VIEWPORT_WIDTH = 15;
    private final static int VIEWPORT_HEIGHT = 15;

    GameStateController gsc;
    TextureController txc;

    OrthographicCamera camera;
    Viewport viewport;

    World world;
    Box2DDebugRenderer b2dr;

    InputController inputController;

    TileMap map;

    SpriteBatch batch;

    public GameScreen(final Client app) throws InvalidPropertiesFormatException {
        super(app);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, VIEWPORT_WIDTH * PPM, VIEWPORT_HEIGHT * PPM);
        viewport = new FitViewport(VIEWPORT_WIDTH * PPM, VIEWPORT_HEIGHT * PPM);

        world = new World(new Vector2(0, 0), false);
        b2dr = new Box2DDebugRenderer();

        PlayerEntity player = new PlayerEntity(world, new PlayerDef());
        player.setPosition(new Vector2(1.5f * PPM, 1.5f * PPM));

        ActionController actionController = new ActionController(player);
        inputController = new InputController(new KeyBinds(), actionController);

        batch = new SpriteBatch();

        map = new TileMap(world, "./map/bomberman_main.tmx");

        gsc = new GameStateController(world, map);

        txc = new TextureController(map);
        gsc.setListener(txc);

        gsc.addPlayers(Arrays.asList(player));

        actionController.addListener(gsc);
    }

    @Override
    public void show() {

    }

    @Override
    public void update(float delta) {
        world.step(1 / 60f, 6, 2);

        if (Gdx.input.isKeyPressed(Input.Keys.O)) {
            camera.zoom -= 1 / PPM;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.P)) {
            camera.zoom += 1 / PPM;
        }

        inputController.update();
        camera.update();

        gsc.update(delta);
        txc.update(delta);

        batch.setProjectionMatrix(camera.combined);
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        batch.begin();
        txc.draw(batch);
        batch.end();

        //b2dr.render(world, camera.combined.cpy().scl(PPM));
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
        gsc.dispose();

        world.dispose();
        b2dr.dispose();
    }
}
