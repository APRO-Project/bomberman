package com.cyberbot.bomberman.models.factories;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.cyberbot.bomberman.core.models.entities.BombEntity;
import com.cyberbot.bomberman.core.models.entities.CollectibleEntity;
import com.cyberbot.bomberman.core.models.entities.Entity;
import com.cyberbot.bomberman.core.models.entities.PlayerEntity;
import com.cyberbot.bomberman.core.models.tiles.Tile;
import com.cyberbot.bomberman.sprites.BombSprite;
import com.cyberbot.bomberman.sprites.CollectibleSprite;
import com.cyberbot.bomberman.sprites.EntitySprite;
import com.cyberbot.bomberman.sprites.PlayerSprite;
import com.cyberbot.bomberman.utils.Atlas;

public class SpriteFactory {
    public static <E extends Entity> EntitySprite<?> createEntitySprite(E entity) {
        if (entity instanceof PlayerEntity) {
            return new PlayerSprite((PlayerEntity) entity);
        } else if (entity instanceof BombEntity) {
            return new BombSprite((BombEntity) entity);
        } else if (entity instanceof CollectibleEntity) {
            return new CollectibleSprite((CollectibleEntity) entity);
        }

        return null;
    }

    public static Sprite createSprite(Tile tile) {
        return Atlas.getInstance().createSprite(tile.getTextureName());
    }

    public static Sprite createSprite(BombEntity entity) {
        return Atlas.getInstance().createSprite("DynamiteStatic");
    }

    public static Sprite createSprite(PlayerEntity player) {
        return new Sprite(new Texture("./textures/player.png"));
    }

    public static Sprite createSprite(CollectibleEntity collectible) {
        switch (collectible.getItemType()) {
            case SMALL_BOMB:
                return Atlas.getInstance().createSprite("DynamiteStatic");
            case UPGRADE_MOVEMENT_SPEED:
                return Atlas.getInstance().createSprite("ArrowFast");
            case UPGRADE_ARMOR:
                return Atlas.getInstance().createSprite("Shield");
            case UPGRADE_REFILL_SPEED:
                // TODO: Replace when texture gets added
                return Atlas.getInstance().createSprite("Player_bbb_idle_back");

        }

        throw new UnsupportedOperationException("Unsupported item type");
    }
}
