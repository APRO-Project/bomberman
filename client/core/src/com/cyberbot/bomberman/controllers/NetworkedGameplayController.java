package com.cyberbot.bomberman.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.cyberbot.bomberman.core.controllers.PlayerMovementController;
import com.cyberbot.bomberman.core.controllers.SnapshotQueue;
import com.cyberbot.bomberman.core.models.Updatable;
import com.cyberbot.bomberman.core.models.defs.PlayerDef;
import com.cyberbot.bomberman.core.models.entities.Entity;
import com.cyberbot.bomberman.core.models.entities.PlayerEntity;
import com.cyberbot.bomberman.core.models.net.Connection;
import com.cyberbot.bomberman.core.models.snapshots.GameSnapshot;
import com.cyberbot.bomberman.core.models.snapshots.PlayerSnapshot;
import com.cyberbot.bomberman.core.models.tiles.MissingLayersException;
import com.cyberbot.bomberman.core.models.tiles.TileMap;
import com.cyberbot.bomberman.models.Drawable;
import com.cyberbot.bomberman.models.KeyBinds;
import com.cyberbot.bomberman.net.GameSnapshotListener;
import com.cyberbot.bomberman.net.NetService;

import java.util.ArrayList;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.cyberbot.bomberman.core.utils.Constants.PPM;

public class NetworkedGameplayController implements Updatable, Drawable, Disposable, GameSnapshotListener {
    private final PlayerEntity localPlayer;

    private final TextureController textureController;
    private final InputController inputController;

    private final World world;
    private final TileMap map;
    private final List<Entity> entities;

    private final SnapshotQueue snapshotQueue;
    private final NetService netService;

    private final ScheduledExecutorService snapshotService;

    private Vector2 playerPosition = new Vector2(0, 0);

    public NetworkedGameplayController(PlayerDef player, String mapPath, Connection connection)
        throws MissingLayersException, InvalidPropertiesFormatException {
        KeyBinds binds = new KeyBinds(); // TODO: Load from preferences

        world = new World(new Vector2(0, 0), false);
        map = new TileMap(world, mapPath);
        localPlayer = new PlayerEntity(world, player, 12345);
        localPlayer.setPosition(new Vector2(1.5f * PPM, 1.5f * PPM));
        entities = new ArrayList<>();
        textureController = new TextureController(map);
        textureController.onEntityAdded(localPlayer);

        PlayerMovementController playerMovement = new PlayerMovementController(localPlayer);
        snapshotQueue = new SnapshotQueue(100);

        inputController = new InputController(binds);
        inputController.addActionController(snapshotQueue);
        inputController.addMovementController(snapshotQueue);
        inputController.addMovementController(playerMovement);

        netService = new NetService(connection, this);
        new Thread(netService).start();

        snapshotService = new ScheduledThreadPoolExecutor(1);
        snapshotService.scheduleAtFixedRate(this::createAndSendSnapshot, 0, 50, TimeUnit.MILLISECONDS);
    }

    @Override
    public void update(float delta) {

        localPlayer.setPosition(playerPosition);

        inputController.update(delta);
        textureController.update(delta);
    }

    @Override
    public void draw(SpriteBatch batch) {
        textureController.draw(batch);
    }

    @Override
    public void dispose() {
        map.dispose();
        world.dispose();
        snapshotService.shutdown();
    }

    @Override
    public void onNewSnapshot(GameSnapshot snapshot) {
        // TODO: Save to a buffer and interpolate
        snapshotQueue.removeUntil(snapshot.sequence);
        snapshot.entities.stream()
            .filter(it -> it.getId() == localPlayer.getId())
            .map(it -> it.getPosition().toVector2())
            .findFirst()
            .ifPresent(vector2 -> playerPosition = vector2);
    }

    private void createAndSendSnapshot() {
        try {
            PlayerSnapshot snapshot = snapshotQueue.createSnapshot();
            netService.sendPlayerSnapshot(snapshot);
        } catch (Exception e) {
            Gdx.app.log("Exception", e.toString());
        }
    }
}
