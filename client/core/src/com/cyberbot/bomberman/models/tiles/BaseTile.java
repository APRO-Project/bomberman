package com.cyberbot.bomberman.models.tiles;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.cyberbot.bomberman.models.Drawable;
import com.cyberbot.bomberman.utils.Atlas;

import java.util.InvalidPropertiesFormatException;

public class BaseTile implements Drawable, Disposable {
    private static final String PROPERTY_TILE_TYPE = "tile_type";
    private static final String PROPERTY_TEXTURE_NAME = "texture";

    private static final String TILE_TYPE_FLOOR = "floor";
    private static final String TILE_TYPE_WALL = "wall";
    private static final String TILE_TYPE_BASE = "base";

    protected final Sprite sprite;

    public BaseTile(Sprite sprite) {
        this.sprite = sprite;
    }

    @Override
    public void dispose() {
        sprite.getTexture().dispose();
    }

    @Override
    public void draw(SpriteBatch batch) {
        sprite.draw(batch);
    }

    public static BaseTile fromMapTile(TiledMapTile tile, World world, Vector2 position)
            throws InvalidPropertiesFormatException {
        MapProperties properties = tile.getProperties();
        if (!properties.containsKey(PROPERTY_TILE_TYPE) || !properties.containsKey(PROPERTY_TEXTURE_NAME)) {
            throw new InvalidPropertiesFormatException(
                    "Each tile in the tile map has to contain '" +
                            PROPERTY_TILE_TYPE + "' and '" +
                            PROPERTY_TEXTURE_NAME + "' properties"
            );
        }

        String tileType = properties.get(PROPERTY_TILE_TYPE, String.class);
        String textureName = properties.get(PROPERTY_TEXTURE_NAME, String.class);
        Sprite sprite = Atlas.getInstance().createSprite(textureName);

        switch (tileType) {
            case TILE_TYPE_FLOOR: {
                return new FloorTile(
                        world, position, sprite,
                        FloorTile.Properties.fromMapProperties(properties)
                );
            }
            case TILE_TYPE_WALL: {
                return new WallTile(world, position, sprite);
            }
            case TILE_TYPE_BASE: {
                return new BaseTile(sprite);
            }
        }

        throw new IllegalArgumentException("Invalid tile type: " + tileType);
    }
}
