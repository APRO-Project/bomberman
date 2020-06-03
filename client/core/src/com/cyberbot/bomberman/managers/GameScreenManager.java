package com.cyberbot.bomberman.managers;

import com.badlogic.gdx.math.Vector2;
import com.cyberbot.bomberman.Client;
import com.cyberbot.bomberman.core.models.items.Inventory;
import com.cyberbot.bomberman.core.models.net.Connection;
import com.cyberbot.bomberman.core.models.net.data.PlayerData;
import com.cyberbot.bomberman.core.models.tiles.MissingLayersException;
import com.cyberbot.bomberman.screens.AbstractScreen;
import com.cyberbot.bomberman.screens.GameScreen;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;

public final class GameScreenManager {

    public enum ScreenState {
        GAME
    }

    private HashMap<ScreenState, AbstractScreen> screens;

    public final Client app;

    public GameScreenManager(final Client app) {
        this.app = app;
        initScreens();

        setScreen(ScreenState.GAME);
    }

    private void initScreens() {
        screens = new HashMap<>();
        try {
            // TODO: Load game screen when a game start packet is received with proper data

            PlayerData playerData = new PlayerData(-1, new Vector2(0, 0), new Inventory(), 0);
            screens.put(ScreenState.GAME, new GameScreen(app, playerData, "./map/bomberman_main.tmx",
                new Connection(12345, InetAddress.getLocalHost())));
        } catch (MissingLayersException | SAXException | ParserConfigurationException | IOException e) {
            e.printStackTrace();
        }
    }

    public void setScreen(ScreenState state) {
        app.setScreen(screens.get(state));
    }
}
