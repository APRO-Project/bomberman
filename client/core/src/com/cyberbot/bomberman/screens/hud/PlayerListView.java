package com.cyberbot.bomberman.screens.hud;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.cyberbot.bomberman.core.models.entities.PlayerEntity;

public final class PlayerListView extends Table {

    PlayerEntity[] players;

    public PlayerListView(Skin skin) {
        players = new PlayerEntity[3];

        Label otherPlayersLabel = new Label("Other players:", skin);
        otherPlayersLabel.setAlignment(Align.center);

        Label player1 = new Label("Henry", skin);
        Label player2 = new Label("John", skin);
        Label player3 = new Label("Adam", skin);

        add(otherPlayersLabel).padBottom(5).colspan(2).row();
        add(new Label("1.", skin)).padRight(5);
        add(player1).row();
        add(new Label("2.", skin)).padRight(5);
        add(player2).row();
        add(new Label("3.", skin)).padRight(5);
        add(player3).row();
    }
}
