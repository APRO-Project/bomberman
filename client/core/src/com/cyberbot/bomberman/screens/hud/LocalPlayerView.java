package com.cyberbot.bomberman.screens.hud;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.cyberbot.bomberman.core.models.entities.PlayerEntity;
import com.cyberbot.bomberman.utils.Atlas;

import static com.cyberbot.bomberman.core.utils.Constants.PPM;

public final class LocalPlayerView extends Table {

    private final Image playerAvatar;
    private final Label playerName;
    private final HealthBar healthBar;

    public LocalPlayerView(PlayerEntity player, Skin skin) {
        // TODO: Get avatar and name from player data
        playerAvatar = new Image(Atlas.getInstance().findRegion("Player_bbb_idle_front"));
        playerName = new Label("James", skin);
        playerName.setAlignment(Align.center);

        healthBar = new HealthBar(player, playerAvatar.getWidth() * 2, PPM / 2);

        add(playerAvatar)
            .padBottom(5)
            .minWidth(playerAvatar.getWidth() * 2)
            .minHeight(playerAvatar.getHeight() * 2);

        add(playerName)
            .expandX()
            .fillX()
            .row();

        add(healthBar);
    }
}
