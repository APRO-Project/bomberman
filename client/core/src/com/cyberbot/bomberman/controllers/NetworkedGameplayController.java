package com.cyberbot.bomberman.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.cyberbot.bomberman.core.controllers.LocalWorldController;
import com.cyberbot.bomberman.core.controllers.SnapshotQueue;
import com.cyberbot.bomberman.core.models.Updatable;
import com.cyberbot.bomberman.core.models.entities.PlayerEntity;
import com.cyberbot.bomberman.core.models.net.data.PlayerData;
import com.cyberbot.bomberman.core.models.net.packets.PlayerSnapshotPacket;
import com.cyberbot.bomberman.core.models.tiles.MissingLayersException;
import com.cyberbot.bomberman.core.models.tiles.TileMap;
import com.cyberbot.bomberman.core.models.tiles.loader.TileMapFactory;
import com.cyberbot.bomberman.models.Drawable;
import com.cyberbot.bomberman.models.KeyBinds;
import com.cyberbot.bomberman.net.NetService;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.SocketAddress;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.cyberbot.bomberman.core.utils.Constants.SIM_RATE;
import static com.cyberbot.bomberman.core.utils.Constants.TICK_RATE;

public class NetworkedGameplayController implements Updatable, Drawable, Disposable {
    private final PlayerEntity localPlayer;

    private final TextureController textureController;
    private final InputController inputController;

    private final LocalWorldController worldController;
    private final TileMap map;

    private final SnapshotQueue snapshotQueue;
    private final NetService netService;

    private final ScheduledExecutorService snapshotService;
    private final ScheduledExecutorService inputPollService;

    public NetworkedGameplayController(PlayerData player, String mapPath, SocketAddress connection)
        throws MissingLayersException, IOException, ParserConfigurationException, SAXException {
        KeyBinds binds = new KeyBinds(); // TODO: Load from preferences

        World world = new World(new Vector2(0, 0), false);
        map = TileMapFactory.createTileMap(world, mapPath);
        localPlayer = player.createEntity(world);
        textureController = new TextureController(map);

        snapshotQueue = new SnapshotQueue(player.getId(), 100);

        inputController = new InputController(binds);
        inputController.addActionController(snapshotQueue);

        worldController = new LocalWorldController(world, TICK_RATE, snapshotQueue, localPlayer);
        worldController.addListener(textureController);

        textureController.onEntityAdded(localPlayer);

        netService = new NetService(connection, worldController);
        new Thread(netService).start();

        snapshotService = new ScheduledThreadPoolExecutor(1);
        snapshotService.scheduleAtFixedRate(this::createAndSendSnapshot,
            0, 1_000_000 / TICK_RATE, TimeUnit.MICROSECONDS);

        inputPollService = new ScheduledThreadPoolExecutor(1);
        inputPollService.scheduleAtFixedRate(inputController::poll,
            0, 1_000_000 / SIM_RATE, TimeUnit.MICROSECONDS);

    }

    @Override
    public void update(float delta) {
        worldController.update(delta);
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
    }

    private void createAndSendSnapshot() {
        try {
            PlayerSnapshotPacket packet = snapshotQueue.createSnapshot();
            netService.sendPlayerSnapshot(packet);
        } catch (Exception e) {
            Gdx.app.log("Exception", e.toString());
        }
    }
}
