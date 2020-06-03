package com.cyberbot.bomberman.controllers;

import com.cyberbot.bomberman.Client;
import com.cyberbot.bomberman.core.models.tiles.MissingLayersException;
import com.cyberbot.bomberman.screens.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
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
                app.setScreen(new LobbyScreen(app, this));
                break;
            case GAME:
                try {
                    app.setScreen(new GameScreen(app, this));
                } catch (MissingLayersException | IOException | ParserConfigurationException | SAXException e) {
                    throw new RuntimeException("Unable to start game");
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + state);
        }
    }
}
