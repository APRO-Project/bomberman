package com.cyberbot.bomberman.screens.hud;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.cyberbot.bomberman.core.models.entities.PlayerEntity;
import com.cyberbot.bomberman.sprites.GraphicsFactory;
import com.cyberbot.bomberman.utils.Atlas;

import static com.cyberbot.bomberman.core.utils.Constants.PPM;

public final class LocalPlayerView extends Table {

    private final Image playerAvatar;
    private final Label playerName;
    private final HealthBar healthBar;

    public LocalPlayerView(Skin skin) {
        playerAvatar = new Image(Atlas.getSkinAtlas().findRegion("placeholder"));
        playerName = new Label("", skin);
        playerName.setAlignment(Align.center);

        healthBar = new HealthBar(playerAvatar.getWidth() * 2, PPM / 2);

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

    public void setPlayerEntity(PlayerEntity entity) {
        healthBar.setPlayerEntity(entity);
        playerAvatar.setDrawable(new TextureRegionDrawable(GraphicsFactory.getPlayerTextureVariant(entity)));
    }

    public void setPlayerName(String name) {
        playerName.setText(name);
    }
}
