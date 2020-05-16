package com.cyberbot.bomberman.core.models.tiles;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import java.util.InvalidPropertiesFormatException;

import static com.cyberbot.bomberman.core.utils.Constants.PPM;

public class Tile {
    private static final String PROPERTY_TILE_TYPE = "tile_type";
    private static final String PROPERTY_TEXTURE_NAME = "texture";

    private static final String TILE_TYPE_FLOOR = "floor";
    private static final String TILE_TYPE_WALL = "wall";
    private static final String TILE_TYPE_BASE = "base";

    private final String textureName;
    private final int x;
    private final int y;

    public Tile(String textureName, Vector2 position) {
        this.textureName = textureName;
        this.x = (int) position.x;
        this.y = (int) position.y;
    }

    public static Tile fromMapTile(TiledMapTile tile, World world, Vector2 position)
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

        switch (tileType) {
            case TILE_TYPE_FLOOR: {
                return new FloorTile(
                    textureName, position,
                    FloorTile.Properties.fromMapProperties(properties)
                );
            }
            case TILE_TYPE_WALL: {
                return new WallTile(textureName, position,
                    WallTile.Properties.fromMapProperties(properties),
                    world
                );
            }
            case TILE_TYPE_BASE: {
                return new Tile(textureName, position);
            }
        }

        throw new IllegalArgumentException("Invalid tile type: " + tileType);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getTextureName() {
        return textureName;
    }

    public Vector2 getPosition() {
        return new Vector2((x + 0.5f) * PPM, (y + 0.5f) * PPM);
    }
}
