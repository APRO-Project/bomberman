package com.cyberbot.bomberman.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.cyberbot.bomberman.core.controllers.LocalWorldController;
import com.cyberbot.bomberman.core.models.Updatable;
import com.cyberbot.bomberman.core.models.net.data.PlayerData;
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
    private final TextureController textureController;
    private final InputController inputController;

    private final LocalWorldController worldController;
    private final TileMap map;

    private final NetService netService;

    private final ScheduledExecutorService snapshotService;
    private final ScheduledExecutorService inputPollService;

    public NetworkedGameplayController(PlayerData player, String mapPath, SocketAddress connection)
        throws MissingLayersException, IOException, ParserConfigurationException, SAXException {
        KeyBinds binds = new KeyBinds(); // TODO: Load from preferences

        World world = new World(new Vector2(0, 0), false);
        map = TileMapFactory.createTileMap(world, mapPath);
        textureController = new TextureController(map);

        worldController = new LocalWorldController(world, TICK_RATE, player);
        worldController.addListener(textureController, true);

        inputController = new InputController(binds);
        inputController.addActionController(worldController);

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
        inputPollService.shutdown();
    }

    private void createAndSendSnapshot() {
        try {
            netService.sendPlayerSnapshot(worldController.createSnapshot());
        } catch (Exception e) {
            // Exceptions thrown in ScheduledThreadPoolExecutor have to be rethrown in the main thread
            Gdx.app.postRunnable(() -> {
                throw e;
            });
        }
    }
}
