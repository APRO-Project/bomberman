package com.cyberbot.bomberman.managers;

import com.cyberbot.bomberman.Client;
import com.cyberbot.bomberman.core.models.tiles.MissingLayersException;
import com.cyberbot.bomberman.screens.AbstractScreen;
import com.cyberbot.bomberman.screens.GameScreen;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;

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
            screens.put(ScreenState.GAME, new GameScreen(app));
        } catch (InvalidPropertiesFormatException | MissingLayersException | JAXBException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void setScreen(ScreenState state) {
        app.setScreen(screens.get(state));
    }
}
