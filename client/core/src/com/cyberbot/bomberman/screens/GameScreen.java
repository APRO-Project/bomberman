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
import com.cyberbot.bomberman.controllers.InputController;
import com.cyberbot.bomberman.controllers.PlayerMovement;
import com.cyberbot.bomberman.models.KeyBinds;
import com.cyberbot.bomberman.models.entities.BombEntity;
import com.cyberbot.bomberman.models.entities.Entity;
import com.cyberbot.bomberman.models.entities.PlayerEntity;
import com.cyberbot.bomberman.models.tiles.TileMap;

import java.util.ArrayList;
import java.util.InvalidPropertiesFormatException;

import static com.cyberbot.bomberman.utils.Constants.PPM;

public class GameScreen extends AbstractScreen {
    OrthographicCamera camera;
    Viewport viewport;
    private final static int VIEWPORT_WIDTH = 15;
    private final static int VIEWPORT_HEIGHT = 15;

    World world;
    Box2DDebugRenderer b2dr;

    PlayerEntity player;
    public ArrayList<BombEntity> bombs = new ArrayList<>();
    InputController inputController;

    TileMap map;

    SpriteBatch batch;

    public GameScreen(final Client app) throws InvalidPropertiesFormatException {
        super(app);

        camera = new OrthographicCamera();
        camera.setToOrtho(false,VIEWPORT_WIDTH * PPM, VIEWPORT_HEIGHT * PPM);
        viewport = new FitViewport(VIEWPORT_WIDTH * PPM, VIEWPORT_HEIGHT * PPM);

        world = new World(new Vector2(0, 0), false);
        b2dr = new Box2DDebugRenderer();

        player = new PlayerEntity(world, "./textures/player.png");
        player.setPosition(new Vector2(1.5f * PPM, 1.5f * PPM));
        player.setGameScreenAndWorld(this, world);

        PlayerMovement movementController = new PlayerMovement(player);
        inputController = new InputController(new KeyBinds(), movementController);

        batch = new SpriteBatch();

        map = new TileMap(world,"./map/bomberman_main.tmx");
    }

    @Override
    public void show() {

    }

    @Override
    public void update(float delta) {
        world.step(1 / 60f, 6, 2);

        if(Gdx.input.isKeyPressed(Input.Keys.O)) {
            camera.zoom -= 1 / PPM;
        }

        if(Gdx.input.isKeyPressed(Input.Keys.P)) {
            camera.zoom += 1 / PPM;
        }

        inputController.update();
        camera.update();
        player.update(delta);

        bombs.forEach(bomb -> bomb.update(delta));

        for(BombEntity bomb : bombs) {
            if(bomb.isBlown())
                bomb.dispose();
        }
        bombs.removeIf(BombEntity::isBlown);

        batch.setProjectionMatrix(camera.combined);
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        batch.begin();
        map.draw(batch);
        bombs.forEach(bomb -> bomb.draw(batch));
        player.draw(batch);
        batch.end();

        b2dr.render(world, camera.combined.cpy().scl(PPM));
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
        player.dispose();
        bombs.forEach(Entity::dispose);

        batch.dispose();

        world.dispose();
        b2dr.dispose();

        map.dispose();
    }
}
