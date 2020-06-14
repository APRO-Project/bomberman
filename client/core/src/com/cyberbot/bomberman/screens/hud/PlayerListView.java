package com.cyberbot.bomberman.screens.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.actions.ColorAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.cyberbot.bomberman.core.models.entities.PlayerEntity;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.ArrayList;
import java.util.List;

public final class PlayerListView extends Table {

    private final List<ImmutablePair<String, Long>> players;
    private final List<ImmutablePair<Label, Label>> labels;

    private static final int MAX_PLAYERS = 3;

    public PlayerListView(Skin skin) {
        players = new ArrayList<>(MAX_PLAYERS);
        labels = new ArrayList<>(MAX_PLAYERS);

        Label otherPlayersLabel = new Label("Other players:", skin);
        otherPlayersLabel.setAlignment(Align.center);
        add(otherPlayersLabel).padBottom(5).colspan(2).row();

        for(int i = 0; i < MAX_PLAYERS; ++i) {
            labels.add(new ImmutablePair<>(new Label(null, skin), new Label(null, skin)));
            labels.get(i).right.setColor(Color.OLIVE);

            add(labels.get(i).left).padRight(5);
            add(labels.get(i).right).row();

            players.add(null);
        }
    }

    public void addPlayer(String name, long id) {
        int emptySlotIndex = -1;

        for(int i = 0; i < MAX_PLAYERS; ++i) {
            if(players.get(i) == null) {
                emptySlotIndex = i;
                break;
            }
        }

        if(emptySlotIndex == -1) {
            Gdx.app.log("boomerman", "Player list full");
            return;
        }

        players.set(emptySlotIndex, new ImmutablePair<>(name, id));

        labels.get(emptySlotIndex).left.setText((emptySlotIndex + 1) + ".");
        labels.get(emptySlotIndex).right.setText(name);
    }

    public void onPlayerDeath(PlayerEntity playerEntity) {
        int nameIndex = -1;

        for(int i = 0; i < MAX_PLAYERS; ++i) {
            if(players.get(i) != null && players.get(i).right == playerEntity.getId()) {
                nameIndex = i;
                break;
            }
        }

        if(nameIndex == -1) {
            Gdx.app.log("boomerman", "Player with ID = " + playerEntity.getId() + " not found in player list");
            return;
        }

        ColorAction action = new ColorAction();
        action.setEndColor(Color.FIREBRICK);
        action.setDuration(3);

        labels.get(nameIndex).right.addAction(action);
    }
}
