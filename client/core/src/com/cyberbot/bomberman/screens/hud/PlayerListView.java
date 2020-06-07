package com.cyberbot.bomberman.screens.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.cyberbot.bomberman.core.models.entities.PlayerEntity;
import org.apache.commons.lang3.tuple.ImmutablePair;

public final class PlayerListView extends Table {

    private final ImmutablePair<String, Long>[] players;
    private final ImmutablePair<Label, Label>[] labels;

    private static final int MAX_PLAYERS = 3;

    public PlayerListView(Skin skin) {
        players = new ImmutablePair[MAX_PLAYERS];
        labels = new ImmutablePair[MAX_PLAYERS];

        Label otherPlayersLabel = new Label("Other players:", skin);
        otherPlayersLabel.setAlignment(Align.center);
        add(otherPlayersLabel).padBottom(5).colspan(2).row();

        for(int i = 0; i < 3; ++i) {
            labels[i] = new ImmutablePair<>(new Label(null, skin), new Label(null, skin));
            add(labels[i].left).padRight(5);
            add(labels[i].right).row();
        }
    }

    public void addPlayer(String name, long id) {
        int emptySlotIndex = -1;

        for(int i = 0; i < players.length; ++i) {
            if(players[i] != null) {
                emptySlotIndex = i;
                break;
            }
        }

        if(emptySlotIndex == -1) {
            Gdx.app.log("boomerman", "Player list full");
            return;
        }

        players[emptySlotIndex] = new ImmutablePair<>(name, id);

        labels[emptySlotIndex].left.setText(String.valueOf(emptySlotIndex));
        labels[emptySlotIndex].right.setText(name);
    }

    public void onPlayerDeath(PlayerEntity playerEntity) {
        int nameIndex = -1;

        for(int i = 0; i < players.length; ++i) {
            if(players[i].right == playerEntity.getId()) {
                nameIndex = i;
                break;
            }
        }

        if(nameIndex == -1) {
            Gdx.app.log("boomerman", "Player with ID = " + playerEntity.getId() + " not found in player list");
            return;
        }

        labels[nameIndex].left.setColor(Color.DARK_GRAY);
        labels[nameIndex].right.setColor(Color.DARK_GRAY);
    }
}
