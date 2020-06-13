package com.cyberbot.bomberman;

import com.badlogic.gdx.Game;
import com.cyberbot.bomberman.controllers.ScreenController;

public class Client extends Game {
    private ScreenController screenController;

    @Override
    public void create() {
        screenController = new ScreenController(this);
    }

    @Override
    public void dispose() {
        // TODO: Dispose everything using the ScreenController
    }
}
