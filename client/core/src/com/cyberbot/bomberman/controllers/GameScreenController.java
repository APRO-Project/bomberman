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

public final class GameScreenController implements ScreenChangeInterface{


    private HashMap<ScreenState, AbstractScreen> screens;

    public final Client app;

    public GameScreenController(final Client app) {
        this.app = app;
        initScreens();

        setScreen(ScreenState.MENU);
    }

    private void initScreens() {
        screens = new HashMap<>();
        screens.put(ScreenState.MENU, new MenuScreen(app, this));
    }

    @Override
    public void setScreen(ScreenState state) {
        switch (state){
            case MENU :
                app.setScreen(screens.get(state));
                break;
            case LOBBY:
                app.setScreen(new LobbyScreen(app, false, this));
                break;
            case GAME:
                try {
                    PlayerData playerData = new PlayerData(-1, new Vector2(0, 0), new Inventory(), 0);
                    app.setScreen(new GameScreen(app,this, playerData, "./map/bomberman_main.tmx",
                        new Connection(12345, InetAddress.getLocalHost())));
                } catch (MissingLayersException | IOException | ParserConfigurationException | SAXException e) {
                    throw new RuntimeException("Unable to start game");
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + state);
        }
    }

    @Override
    public void setScreen(ScreenState state, String playerName, boolean isOwner) {
        if (!state.equals(ScreenState.LOBBY)){
            throw new IllegalArgumentException("Only lobby screen can contain player nickname");
        }
        app.setScreen(new LobbyScreen(app, this, playerName, isOwner));
    }
}
