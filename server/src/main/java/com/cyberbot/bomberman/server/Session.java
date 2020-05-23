package com.cyberbot.bomberman.server;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.cyberbot.bomberman.core.controllers.GameStateController;
import com.cyberbot.bomberman.core.controllers.PlayerActionController;
import com.cyberbot.bomberman.core.models.actions.Action;
import com.cyberbot.bomberman.core.models.defs.PlayerDef;
import com.cyberbot.bomberman.core.models.entities.PlayerEntity;
import com.cyberbot.bomberman.core.models.snapshots.GameSnapshot;
import com.cyberbot.bomberman.core.models.snapshots.PlayerSnapshot;
import com.cyberbot.bomberman.core.models.tiles.MissingLayersException;
import com.cyberbot.bomberman.core.models.tiles.TileMap;
import com.cyberbot.bomberman.core.utils.Utils;

import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.cyberbot.bomberman.core.utils.Constants.PPM;
import static com.cyberbot.bomberman.core.utils.Constants.SIM_RATE;

public class Session {
    private final List<ClientConnection> clients = new ArrayList<>();

    private long lastUpdate;
    private PlayerSnapshot playerSnapshot;
    private GameStateController gameStateController;
    private PlayerActionController actionController;
    private PlayerEntity playerEntity;
    private World world;

    private Queue<List<Action>> actionsQueue = new ArrayDeque<>();

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
        playerEntity = new PlayerEntity(world, new PlayerDef(0), 12345);
        actionController = new PlayerActionController(playerEntity);
        playerEntity.setPosition(new Vector2(1.5f * PPM, 1.5f * PPM));
        gameStateController.addPlayers(Collections.singleton(playerEntity));
        playerSnapshot = new PlayerSnapshot(0);

        actionController.addListener(gameStateController);

        ScheduledExecutorService updateService = new ScheduledThreadPoolExecutor(1);
        updateService.scheduleAtFixedRate(() -> {
            long t0 = System.currentTimeMillis();
            update((t0 - lastUpdate) / 1_000f);
            lastUpdate = t0;
        }, 0, 1_000_000 / SIM_RATE, TimeUnit.MICROSECONDS);
    }

    public boolean handlePacket(ClientConnection connection, byte[] data, int length) {
        if (!hasClient(connection)) {
            return false;
        }

        PlayerSnapshot newSnapshot = Utils.fromByteArray(data, PlayerSnapshot.class);
        if (newSnapshot != null) { //&& Utils.isSequenceNext(newSnapshot.sequence, playerSnapshot.sequence)) {
            this.playerSnapshot = newSnapshot;
            actionsQueue.addAll(playerSnapshot.actions);
        }

        return true;
    }

    public byte[] getState() {
        GameSnapshot snapshot = gameStateController.createSnapshot();
        // TODO Create separate for all clients;
        GameSnapshot customizedSnapshot = new GameSnapshot(snapshot, playerEntity.getData(), playerSnapshot.sequence);
        return Utils.toByteArray(customizedSnapshot);
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
        List<Action> actions = actionsQueue.poll();
        if (actions != null) {
            actionController.onActions(actions);
        }
        gameStateController.update(delta);
    }
}
