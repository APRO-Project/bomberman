package com.cyberbot.bomberman.screens.hud;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.cyberbot.bomberman.core.models.entities.PlayerEntity;
import com.cyberbot.bomberman.utils.Atlas;

public class PlayerListView extends Table {

    PlayerEntity[] players;

    private Label timeLabel;
    private float time;

    public PlayerListView(Skin skin) {
        players = new PlayerEntity[3];
        time = 0;

        init(skin);
    }

    private void init(Skin skin) {
        Label timeTitle = new Label("Time:", skin);
        timeTitle.setAlignment(Align.center);

        timeLabel = new Label("00:00", skin);
        timeLabel.setAlignment(Align.center);

        NinePatch separator = new NinePatch(Atlas.getSkinAtlas().findRegion("separator"));

        add(timeTitle)
            .expandX()
            .fillX()
            .padTop(10)
            .center()
            .row();

        add(timeLabel)
            .expandX()
            .fillX()
            .space(10)
            .center()
            .row();

        add(new Image(separator))
            .expandX()
            .fillX()
            .pad(10)
            .maxHeight(2)
            .prefHeight(2)
            .row();

        Table playerList = new Table();

        Label otherPlayersLabel = new Label("Other players:", skin);
        otherPlayersLabel.setAlignment(Align.center);

        Label player1 = new Label("Henry", skin);
        Label player2 = new Label("John", skin);
        Label player3 = new Label("Adam", skin);

        playerList.add(otherPlayersLabel).padBottom(5).colspan(2).row();
        playerList.add(new Label("1.", skin)).padRight(5);
        playerList.add(player1).row();
        playerList.add(new Label("2.", skin)).padRight(5);
        playerList.add(player2).row();
        playerList.add(new Label("3.", skin)).padRight(5);
        playerList.add(player3).row();

        add(playerList).expand().fill();
    }

    private void updateTimer() {
        int seconds = (int) time;
        int minutes = seconds / 60;
        seconds %= 60;

        timeLabel.setText(String.format("%02d:%02d", minutes, seconds));
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        time += delta;
        updateTimer();
    }
}
