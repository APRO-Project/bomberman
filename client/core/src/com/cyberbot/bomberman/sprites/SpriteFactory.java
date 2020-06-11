package com.cyberbot.bomberman.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.cyberbot.bomberman.core.models.entities.BombEntity;
import com.cyberbot.bomberman.core.models.entities.CollectibleEntity;
import com.cyberbot.bomberman.core.models.entities.Entity;
import com.cyberbot.bomberman.core.models.entities.PlayerEntity;
import com.cyberbot.bomberman.core.models.items.ItemType;
import com.cyberbot.bomberman.core.models.tiles.Tile;
import com.cyberbot.bomberman.core.models.tiles.TileMapLayer;
import com.cyberbot.bomberman.utils.Atlas;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Factory for {@link Sprite} and {@link EntitySprite} objects.
 */
public class SpriteFactory {
    /**
     * Creates an {@link EntitySprite} matching the provided entity.
     *
     * @param entity The source entity.
     * @param <E>    The entity managed by the EntitySprite.
     * @return A new EntitySprite.
     */
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
        switch (getBombVariant(entity)) {
            case BombSprite.VARIANT_SMALL_RED:
                return Atlas.getInstance().createSprite("DynamiteStatic");
            case BombSprite.VARIANT_MEDIUM_RED:
                return Atlas.getInstance().createSprite("Player_wrb_idle_right"); // TODO: Replace with valid texture
        }

        throw new IllegalArgumentException("Invalid texture variant " + entity.getPlayerTextureVariant());
    }

    public static Sprite createSprite(PlayerEntity player) {
        return new Sprite(new Texture("./textures/player.png"));
    }

    public static Sprite createSprite(CollectibleEntity collectible) {
        switch (collectible.getItemType()) {
            case SMALL_BOMB:
                return Atlas.getInstance().createSprite("DynamiteStatic");
            case MEDIUM_BOMB:
                return Atlas.getInstance().createSprite("Player_wrb_idle_right");
            case UPGRADE_MOVEMENT_SPEED:
                return Atlas.getInstance().createSprite("ArrowFast");
            case UPGRADE_ARMOR:
                return Atlas.getInstance().createSprite("Shield");
            case UPGRADE_REFILL_SPEED:
                // TODO: Replace when texture gets added
                return Atlas.getInstance().createSprite("Player_bbb_idle_back");

        }

        throw new IllegalArgumentException("Unsupported item type");
    }

    public static List<TileSprite> createTilesFromMapLayer(TileMapLayer layer) {
        return layer.stream().map(TileSprite::new).collect(Collectors.toList());
    }

    public static int getBombVariant(BombEntity bombEntity) {
        ItemType type = bombEntity.getBombItemType();
        switch (type) {
            case SMALL_BOMB:
                return BombSprite.VARIANT_SMALL_RED; // TODO: Check the player texture variant
            case MEDIUM_BOMB:
                return BombSprite.VARIANT_MEDIUM_RED; // TODO: Check the player texture variant
        }

        throw new IllegalArgumentException("Not a bomb item type " + type);
    }
}
