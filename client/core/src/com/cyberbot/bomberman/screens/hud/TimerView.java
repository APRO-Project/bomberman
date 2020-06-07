package com.cyberbot.bomberman.screens.hud;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

public final class TimerView extends Table {

    private final Label timeLabel;
    private float elapsedTime;

    public TimerView(Skin skin) {
        Label title = new Label("Time:", skin);
        title.setAlignment(Align.center);

        timeLabel = new Label(null, skin);
        timeLabel.setAlignment(Align.center);

        add(title).row();
        add(timeLabel);

        elapsedTime = 0;
    }

    private void updateTimeLabel() {
        int seconds = (int) elapsedTime;
        int minutes = seconds / 60;
        seconds %= 60;

        timeLabel.setText(String.format("%02d:%02d", minutes, seconds));
    }

    @Override
    public void act(float delta) {
        elapsedTime += delta;
        updateTimeLabel();
    }
}
