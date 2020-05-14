package com.cyberbot.bomberman.managers;

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.cyberbot.bomberman.Client;
import com.cyberbot.bomberman.screens.AbstractScreen;
import com.cyberbot.bomberman.screens.GameScreen;

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
        } catch (InvalidPropertiesFormatException e) {
            e.printStackTrace();
        }
    }

    public void setScreen(ScreenState state) {
        app.setScreen(screens.get(state));
    }
}