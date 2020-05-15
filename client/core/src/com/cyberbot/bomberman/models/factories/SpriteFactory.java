package com.cyberbot.bomberman.models.factories;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.cyberbot.bomberman.models.entities.BombEntity;
import com.cyberbot.bomberman.models.entities.PlayerEntity;
import com.cyberbot.bomberman.models.tiles.Tile;
import com.cyberbot.bomberman.utils.Atlas;

public class SpriteFactory {
    public static Sprite createSprite(Tile tile) {
        return Atlas.getInstance().createSprite(tile.getTextureName());
    }

    public static Sprite createSprite(BombEntity entity) {
        return Atlas.getInstance().createSprite("DynamiteStatic");
    }

    public static Sprite createSprite(PlayerEntity player) {
        return new Sprite(new Texture("./textures/player.png"));
    }
}
