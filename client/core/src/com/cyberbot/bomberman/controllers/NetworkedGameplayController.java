package com.cyberbot.bomberman.controllers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.cyberbot.bomberman.core.controllers.LocalWorldController;
import com.cyberbot.bomberman.core.models.Updatable;
import com.cyberbot.bomberman.core.models.net.data.PlayerData;
import com.cyberbot.bomberman.core.models.tiles.MapLoadException;
import com.cyberbot.bomberman.core.models.tiles.TileMap;
import com.cyberbot.bomberman.core.models.tiles.loader.TileMapFactory;
import com.cyberbot.bomberman.models.Drawable;
import com.cyberbot.bomberman.models.KeyBinds;
import com.cyberbot.bomberman.net.NetService;
import com.cyberbot.bomberman.screens.hud.GameHud;

import java.net.SocketAddress;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import static com.cyberbot.bomberman.core.utils.Constants.SIM_RATE;
import static com.cyberbot.bomberman.core.utils.Constants.TICK_RATE;

public class NetworkedGameplayController implements Updatable, Drawable, Disposable {
    private final TextureController textureController;
    private final InputController inputController;

    private final LocalWorldController worldController;
    private final TileMap map;

    private final NetService netService;

    private final ScheduledExecutorService snapshotService;
    private final ScheduledExecutorService inputPollService;

    private final World world;

    private final ReentrantLock worldUpdateLock;
    private final Condition worldUpdatedCondition;

    private final GameHud hud;

    public NetworkedGameplayController(PlayerData player, String mapPath,
                                       SocketAddress connection, GameHud hud)
        throws MapLoadException {
        KeyBinds binds = new KeyBinds(); // TODO: Load from preferences

        world = new World(new Vector2(0, 0), false);
        map = TileMapFactory.createTileMap(world, mapPath);
        textureController = new TextureController(map);

        worldController = new LocalWorldController(world, map, TICK_RATE, player);
        worldController.addListener(textureController, true);

        this.hud = hud;

        inputController = new InputController(binds, this.hud);
        inputController.addActionController(worldController);

        netService = new NetService(connection, worldController);
        new Thread(netService).start();

        worldUpdateLock = new ReentrantLock();
        worldUpdatedCondition = worldUpdateLock.newCondition();

        snapshotService = new ScheduledThreadPoolExecutor(1);
        snapshotService.scheduleAtFixedRate(this::createAndSendSnapshot,
            0, 1_000_000 / TICK_RATE, TimeUnit.MICROSECONDS);

        inputPollService = new ScheduledThreadPoolExecutor(1);
        inputPollService.scheduleAtFixedRate(inputController::poll,
            0, 1_000_000 / SIM_RATE, TimeUnit.MICROSECONDS);
    }

    @Override
    public void update(float delta) {
        try {
            worldUpdateLock.lock();
            worldController.update(delta);
            worldUpdatedCondition.signalAll();
        } finally {
            worldUpdateLock.unlock();
        }

        textureController.update(delta);
        hud.act(delta);
    }

    @Override
    public void draw(SpriteBatch batch) {
        textureController.draw(batch);
    }

    @Override
    public void dispose() {
        map.dispose();
        worldController.dispose();
        snapshotService.shutdown();
        inputPollService.shutdown();
        hud.dispose();
    }

    public GameHud getHud() {
        return hud;
    }

    public World getWorld() {
        return world;
    }

    private void createAndSendSnapshot() {
        try {
            try {
                worldUpdateLock.lock();
                worldUpdatedCondition.await();
            } finally {
                worldUpdateLock.unlock();
            }

            netService.sendPlayerSnapshot(worldController.createSnapshot());
        } catch (Exception ignored) {

        }
    }
}
