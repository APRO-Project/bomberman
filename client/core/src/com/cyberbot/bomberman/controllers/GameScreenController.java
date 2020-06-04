package com.cyberbot.bomberman.controllers;

import com.badlogic.gdx.math.Vector2;
import com.cyberbot.bomberman.Client;
import com.cyberbot.bomberman.core.models.items.Inventory;
import com.cyberbot.bomberman.core.models.net.Connection;
import com.cyberbot.bomberman.core.models.net.data.PlayerData;
import com.cyberbot.bomberman.core.models.tiles.MissingLayersException;
import com.cyberbot.bomberman.screens.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;

public final class GameScreenController implements ScreenChangeInterface {

    public final Client app;

    public GameScreenController(final Client app) {
        this.app = app;

        setMenuScreen();
    }

    @Override
    public void setMenuScreen() {
        app.setScreen(new MenuScreen(this));
    }

    @Override
    public void setGameScreen() {
        try {
            // TODO: Load game screen when a game start packet is received with proper data
            PlayerData playerData = new PlayerData(-1, new Vector2(0, 0), new Inventory(), 0);
            app.setScreen(new GameScreen(this, playerData, "./map/bomberman_main.tmx",
                new Connection(12345, InetAddress.getLocalHost())));
        } catch (MissingLayersException | IOException | ParserConfigurationException | SAXException e) {
            throw new RuntimeException("Unable to start game");
        }
    }

    @Override
    public void setLobbyScreen(String playerName, boolean isOwner) {
        app.setScreen(new LobbyScreen(this, playerName, isOwner));
    }

    @Override
    public void setLobbyScreen(boolean isOwner) {
        app.setScreen(new LobbyScreen(this, isOwner));
    }
}
