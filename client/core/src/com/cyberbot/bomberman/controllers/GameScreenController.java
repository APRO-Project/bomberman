package com.cyberbot.bomberman.controllers;

import com.cyberbot.bomberman.Client;
import com.cyberbot.bomberman.core.models.tiles.MissingLayersException;
import com.cyberbot.bomberman.screens.AbstractScreen;
import com.cyberbot.bomberman.screens.GameScreen;
import com.cyberbot.bomberman.screens.MenuScreen;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.HashMap;

public final class GameScreenController {


    private HashMap<ScreenState, AbstractScreen> screens;

    public final Client app;

    public GameScreenController(final Client app) {
        this.app = app;
        initScreens();

        setScreen(ScreenState.MENU);
    }

    private void initScreens() {
        screens = new HashMap<>();
        try {
            screens.put(ScreenState.GAME, new GameScreen(app));
            screens.put(ScreenState.MENU, new MenuScreen(app));
        } catch (MissingLayersException | SAXException | ParserConfigurationException | IOException e) {
            e.printStackTrace();
        }
    }

    public void setScreen(ScreenState state) {
        app.setScreen(screens.get(state));
    }
}
