package com.cyberbot.bomberman.sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.cyberbot.bomberman.core.models.entities.BombEntity;
import com.cyberbot.bomberman.core.models.entities.CollectibleEntity;
import com.cyberbot.bomberman.core.models.entities.Entity;
import com.cyberbot.bomberman.core.models.entities.PlayerEntity;
import com.cyberbot.bomberman.core.models.items.ItemType;
import com.cyberbot.bomberman.core.models.tiles.Tile;
import com.cyberbot.bomberman.core.models.tiles.TileMapLayer;
import com.cyberbot.bomberman.utils.Atlas;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Factory for {@link Sprite} and {@link EntitySprite} objects.
 */
public class GraphicsFactory {

    private static final Map<ItemType, String> collectibleTexturePaths = Map.of(
        ItemType.SMALL_BOMB, "DynamiteStatic",
        ItemType.MEDIUM_BOMB, "Player_wrb_idle_right",
        ItemType.UPGRADE_ARMOR,"Shield",
        ItemType.UPGRADE_MOVEMENT_SPEED,"ArrowFast",
        ItemType.UPGRADE_REFILL_SPEED,"Player_bbb_idle_back"
    );

    private static final List<String> playerVariants = List.of("bbb", "brw", "wbw", "wrb");

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
        return Atlas.getInstance()
            .createSprite("Player_" + playerVariants.get(player.getTextureVariant()) + "_idle_front");
    }

    public static TextureRegion getPlayerTextureVariant(PlayerEntity player) {
        String texturePath = "Player_" + playerVariants.get(player.getTextureVariant()) + "_idle_";

        switch (player.facingDirection) {
            case RIGHT:
                texturePath += "right";
                break;
            case LEFT:
                texturePath += "left";
                break;
            case BACK:
                texturePath += "back";
                break;
            case FRONT:
                texturePath += "front";
                break;
        }

        return Atlas.getInstance().findRegion(texturePath);
    }

    public static Sprite createSprite(CollectibleEntity collectible) {
        String texturePath = collectibleTexturePaths.get(collectible.getItemType());
        if(texturePath == null) {
            throw new IllegalArgumentException("Unsupported item type");
        }

        return Atlas.getInstance().createSprite(texturePath);
    }

    public static TextureRegion getCollectibleTextureRegion(ItemType type) {
        String texturePath = collectibleTexturePaths.get(type);
        if(texturePath == null) {
            throw new IllegalArgumentException("Given item type is not a collectible type or is not yet supported");
        }

        return Atlas.getInstance().findRegion(texturePath);
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
