package com.cyberbot.bomberman.controllers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.cyberbot.bomberman.core.controllers.LocalWorldController;
import com.cyberbot.bomberman.core.models.Updatable;
import com.cyberbot.bomberman.core.models.entities.PlayerEntity;
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

/**
 * The main orchestrator for a networked gameplay. It schedules input polling and snapshot creation.
 * The actual world simulation, interpolation, rollback on other world related actions are handled
 * by a corresponding {@link LocalWorldController}.
 * <p>
 * The {@link #update(float)} method can be called with arbitrary period.
 * <p>
 * If the next snapshot creation falls in the middle of world simulation
 * the snapshot thread will wait for the simulation to finish.
 */
public class NetworkedGameplayController implements Updatable, Drawable, Disposable {
    private final TextureController textureController;

    private final LocalWorldController worldController;
    private final TileMap map;

    private final NetService netService;

    private final ScheduledExecutorService snapshotService;
    private final ScheduledExecutorService inputPollService;

    private final ReentrantLock worldUpdateLock;
    private final Condition worldUpdatedCondition;

    public NetworkedGameplayController(PlayerData localPlayer, String mapPath,
                                       SocketAddress connection, GameHud hud)
        throws MapLoadException {
        KeyBinds binds = new KeyBinds(); // TODO: Load from preferences

        World world = new World(new Vector2(0, 0), false);
        map = TileMapFactory.createTileMap(world, mapPath);
        textureController = new TextureController(map);

        // TODO: Maybe allow variable tick rate, sim rate via server's JSON configuration
        worldController = new LocalWorldController(world, map, TICK_RATE, SIM_RATE, localPlayer);
        worldController.addListener(textureController, true);
        worldController.addListener(hud, true);

        InputController inputController = new InputController(binds, hud);
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
    }

    private void createAndSendSnapshot() {
        try {
            try {
                worldUpdateLock.lock();
                while (worldController.isWorldLocked()) {
                    worldUpdatedCondition.await();
                }
            } finally {
                worldUpdateLock.unlock();
            }

            netService.sendPlayerSnapshot(worldController.createSnapshot());
        } catch (Exception e) {
            // Exceptions thrown in the ScheduledExecutorService are caught
            // and returned in a Future only when the executor service is stopped.
            // This is the simplest way to prevent the service from halting and
            // getting any debugging information from the exceptions
            e.printStackTrace();
        }
    }

    public PlayerEntity getLocalPlayer() {
        return worldController.getLocalPlayer();
    }
}
