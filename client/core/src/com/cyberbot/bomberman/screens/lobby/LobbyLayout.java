package com.cyberbot.bomberman.screens.lobby;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cyberbot.bomberman.controllers.GameScreenController;

public class LobbyLayout extends Stage {

    final GameScreenController gameScreenController;

    public LobbyLayout(Viewport viewport, GameScreenController gameScreenController) {
        super(viewport);
        this.gameScreenController = gameScreenController;
    }

    public void createLobbyUi(){
        Table ui = new Table();
        ui.setDebug(true);


        float worldWidth = getViewport().getWorldWidth();
        float worldHeight = getViewport().getWorldHeight();
        float tableWidth = worldWidth / 3;

        ui.setPosition((worldWidth - tableWidth) / 2, 1);
        ui.setWidth(tableWidth);
        ui.setHeight(worldHeight);
        addActor(ui);
    }
}
