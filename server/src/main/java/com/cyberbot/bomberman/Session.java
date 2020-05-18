package com.cyberbot.bomberman;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.cyberbot.bomberman.core.controllers.GameStateController;
import com.cyberbot.bomberman.core.controllers.PlayerActionController;
import com.cyberbot.bomberman.core.controllers.PlayerMovementController;
import com.cyberbot.bomberman.core.models.defs.PlayerDef;
import com.cyberbot.bomberman.core.models.entities.PlayerEntity;
import com.cyberbot.bomberman.core.models.snapshots.GameSnapshot;
import com.cyberbot.bomberman.core.models.snapshots.PlayerSnapshot;
import com.cyberbot.bomberman.core.models.tiles.MissingLayersException;
import com.cyberbot.bomberman.core.models.tiles.TileMap;
import com.cyberbot.bomberman.core.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.cyberbot.bomberman.core.utils.Constants.PPM;

public class Session {
    private final List<ClientConnection> clients = new ArrayList<>();

    private long lastUpdate;

    private GameSnapshot snapshot;
    private PlayerSnapshot playerSnapshot;
    private GameStateController gameStateController;
    private PlayerActionController actionController;
    private PlayerMovementController playerMovementController;
    private PlayerEntity playerEntity;
    private World world;

    public Session() {
        this.world = new World(new Vector2(0, 0), false);
        TileMap map = null;
        try {
            map = new TileMap(world, "./map/bomberman_main.tmx");
        } catch (InvalidPropertiesFormatException | MissingLayersException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        this.gameStateController = new GameStateController(world, map);
        lastUpdate = System.currentTimeMillis();
        playerEntity = new PlayerEntity(world, new PlayerDef());
        actionController = new PlayerActionController(playerEntity);
        playerMovementController = new PlayerMovementController(playerEntity);
        playerEntity.setPosition(new Vector2(1.5f * PPM, 1.5f * PPM));
        gameStateController.addPlayers(Collections.singleton(playerEntity));
        playerSnapshot = new PlayerSnapshot(0);
        snapshot = new GameSnapshot(0);
        snapshot.position = playerEntity.getPosition();

        ScheduledExecutorService updateService = new ScheduledThreadPoolExecutor(1);
        updateService.scheduleAtFixedRate(() -> {
            long t0 = System.currentTimeMillis();
            update((t0 - lastUpdate) / 1_000f);
            lastUpdate = t0;
        }, 0, 1_000_000 / 60, TimeUnit.MICROSECONDS);
    }

    public boolean handlePacket(ClientConnection connection, byte[] data, int length) {
        if (!hasClient(connection)) {
            return false;
        }

        PlayerSnapshot newSnapshot = Utils.fromByteArray(data, PlayerSnapshot.class);
        if (newSnapshot != null && Utils.isSequenceNext(newSnapshot.sequence, playerSnapshot.sequence)) {
            this.playerSnapshot = newSnapshot;
        }

        return true;
    }

    public byte[] getState() {
        return Utils.toByteArray(gameStateController.createSnapshot(playerSnapshot.sequence));
    }

    public Iterable<ClientConnection> getClients() {
        return clients;
    }

    public void addClient(ClientConnection connection) {
        clients.add(connection);
    }

    public boolean removeClient(ClientConnection connection) {
        return clients.remove(connection);
    }

    public boolean hasClient(ClientConnection connection) {
        return clients.contains(connection);
    }

    private synchronized void update(float delta) {
        world.step(delta, 6, 2);
        playerMovementController.move(playerSnapshot.movingDirection);
        gameStateController.update(delta);
    }
}
