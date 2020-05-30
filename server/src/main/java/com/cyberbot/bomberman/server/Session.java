package com.cyberbot.bomberman.server;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.cyberbot.bomberman.core.controllers.GameStateController;
import com.cyberbot.bomberman.core.models.defs.PlayerDef;
import com.cyberbot.bomberman.core.models.entities.PlayerEntity;
import com.cyberbot.bomberman.core.models.net.packets.GameSnapshotPacket;
import com.cyberbot.bomberman.core.models.net.packets.PlayerSnapshotPacket;
import com.cyberbot.bomberman.core.models.net.snapshots.GameSnapshot;
import com.cyberbot.bomberman.core.models.tiles.MissingLayersException;
import com.cyberbot.bomberman.core.models.tiles.TileMap;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.cyberbot.bomberman.core.utils.Constants.SIM_RATE;
import static com.cyberbot.bomberman.core.utils.Constants.TICK_RATE;

public class Session {
    private final Map<ClientConnection, PlayerSession> clientSessions = new HashMap<>();
    private final GameStateController gameStateController;
    private final World world;

    private final ScheduledExecutorService simulationService;
    private final ScheduledExecutorService tickService;
    private final GameSocket socket;

    private long lastUpdate;
    private boolean gameStarted;

    public Session(GameSocket socket) throws MissingLayersException, InvalidPropertiesFormatException {
        this.socket = socket;
        this.world = new World(new Vector2(0, 0), false);
        TileMap map = new TileMap(world, "./map/bomberman_main.tmx");

        this.gameStateController = new GameStateController(world, map);

        gameStarted = false;

        lastUpdate = System.currentTimeMillis();

        simulationService = new ScheduledThreadPoolExecutor(1);
        tickService = new ScheduledThreadPoolExecutor(1);
    }

    public boolean onSnapshot(ClientConnection connection, PlayerSnapshotPacket packet) {
        if (!hasClient(connection)) {
            throw new RuntimeException("Client not part of this session");
        }

        clientSessions.get(connection).onNewSnapshot(packet);

        return true;
    }

    public void addClient(ClientConnection connection) {
        if (gameStarted) {
            throw new IllegalStateException("The game has already started, cannot add clients");
        }

        int playerIndex = clientSessions.size();
        PlayerEntity playerEntity = new PlayerEntity(
            world,
            new PlayerDef(playerIndex),
            gameStateController.generateEntityId());

        gameStateController.addPlayer(playerEntity);
        clientSessions.put(connection, new PlayerSession(playerEntity));
    }

    public boolean removeClient(ClientConnection connection) {
        if (gameStarted) {
            throw new IllegalStateException("The game has already started, cannot remove clients");
        }

        return clientSessions.remove(connection) != null;
    }

    public boolean hasClient(ClientConnection connection) {
        return clientSessions.containsKey(connection);
    }

    private void tick() {
        for (Map.Entry<PlayerSession, DatagramPacket> entry : getUpdatePackets().entrySet()) {
            DatagramPacket packet = entry.getValue();
            PlayerSession session = entry.getKey();
            try {
                socket.send(packet);
                session.clearErrors();
            } catch (IOException e) {
                session.onError();
            }
        }
    }

    private Map<PlayerSession, DatagramPacket> getUpdatePackets() {
        final GameSnapshot snapshot = gameStateController.createSnapshot();

        return clientSessions.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, entry -> {
            ClientConnection connection = entry.getKey();
            PlayerSession playerSession = entry.getValue();

            GameSnapshotPacket packet = new GameSnapshotPacket(playerSession.getSequence(), snapshot);
            byte[] payload = packet.toByteArray();

            return new DatagramPacket(payload, payload.length, connection.getAddress(), connection.getPort());
        }));
    }

    private void startGame() {
        if (gameStarted) {
            throw new IllegalStateException("The game hasalready been started");
        }

        gameStarted = true;
        scheduleSimulationUpdates();
        scheduleTickUpdates();
    }

    private void pauseGame() {
        if (!gameStarted) {
            throw new IllegalStateException("The game has not yet been started");
        }

        simulationService.shutdown();
        tickService.shutdown();
    }

    private void scheduleSimulationUpdates() {
        simulationService.scheduleAtFixedRate(() -> {
            long t0 = System.currentTimeMillis();
            update((t0 - lastUpdate) / 1_000f);
            lastUpdate = t0;
        }, 0, 1_000_000 / SIM_RATE, TimeUnit.MICROSECONDS);
    }

    private void scheduleTickUpdates() {
        simulationService.scheduleAtFixedRate(
            this::tick, 0,
            1_000_000 / TICK_RATE, TimeUnit.MICROSECONDS);
    }

    private static Vector2 getPlayerSpawnPosition(int playerIndex, int mapSize) {
        // TODO: Store spawn information in the map file
        switch (playerIndex) {
            case 0:
                return new Vector2(1.5f, mapSize - 1.5f);
            case 1:
                return new Vector2(mapSize - 1.5f, 1.5f);
            case 2:
                return new Vector2(mapSize - 1.5f, mapSize - 1.5f);
            case 3:
                return new Vector2(1.5f, 1.5f);
            default:
                throw new IllegalArgumentException("Invalid player index");
        }
    }

    private synchronized void update(float delta) {
        world.step(delta, 6, 2);
        for (PlayerSession session : clientSessions.values()) {
            session.update(delta);
        }
        gameStateController.update(delta);
    }
}
