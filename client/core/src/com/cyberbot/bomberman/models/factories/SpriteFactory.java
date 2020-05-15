package com.cyberbot.bomberman.models.factories;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.cyberbot.bomberman.models.entities.BombEntity;
import com.cyberbot.bomberman.models.entities.Entity;
import com.cyberbot.bomberman.models.entities.PlayerEntity;
import com.cyberbot.bomberman.models.tiles.Tile;
import com.cyberbot.bomberman.utils.Atlas;

public class SpriteFactory {
    public static Sprite createSprite(Tile tile) {
        return Atlas.getInstance().createSprite(tile.getTextureName());
    }

    public static Sprite createSprite(Entity entity) {
        if(entity instanceof PlayerEntity) {
            return forPlayer((PlayerEntity) entity);
        }

        if(entity instanceof BombEntity) {
            return Atlas.getInstance().createSprite("DynamiteStatic");
        }

        return null;
    }

    private static Sprite forPlayer(PlayerEntity player) {
        // TODO: Implement animations and texture variants
        return new Sprite(new Texture("./textures/player.png"));
    }
}
