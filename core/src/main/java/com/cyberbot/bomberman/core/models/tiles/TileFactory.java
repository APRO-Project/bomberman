package com.cyberbot.bomberman.core.models.tiles;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.physics.box2d.World;

import java.util.InvalidPropertiesFormatException;

/**
 * Factory for the {@link Tile Tile} objects.
 */
public class TileFactory {
    /**
     * Factory method for creating new Tiles from a LibGDX {@link TiledMapTile TiledMapTile}.
     *
     * @param tile  The reference TiledMapTile.
     * @param world Box2D world reference, required for any {@link PhysicalTile PhysicalTiles}.
     * @param x     The x component of the tile's position.
     * @param y     The y component of the tile's position.
     * @return A new instance of a Tile.
     * @throws InvalidPropertiesFormatException When some required properties where missing from the TiledMapTile
     *                                          or were of an invalid type.
     * @throws IllegalArgumentException         When a property contains an illegal value.
     */
    public static Tile createTile(TiledMapTile tile, World world, int x, int y)
        throws InvalidPropertiesFormatException {
        MapProperties properties = tile.getProperties();
        if (!properties.containsKey(Tile.PROPERTY_TILE_TYPE) ||
            !properties.containsKey(Tile.PROPERTY_TEXTURE_NAME)) {
            throw new InvalidPropertiesFormatException(
                "Each tile in the tile map has to contain '" +
                    Tile.PROPERTY_TILE_TYPE + "' and '" +
                    Tile.PROPERTY_TEXTURE_NAME + "' properties"
            );
        }

        String tileType = properties.get(Tile.PROPERTY_TILE_TYPE, String.class);
        String textureName = properties.get(Tile.PROPERTY_TEXTURE_NAME, String.class);

        switch (tileType) {
            case Tile.TILE_TYPE_FLOOR:
                return new FloorTile(
                    textureName,
                    FloorTile.Properties.fromMapProperties(properties),
                    x, y
                );
            case Tile.TILE_TYPE_WALL:
                return new WallTile(world, textureName,
                    WallTile.Properties.fromMapProperties(properties),
                    x, y
                );
            case Tile.TILE_TYPE_BASE:
                return new Tile(textureName, x, y);
            default:
                throw new IllegalArgumentException("Invalid tile type: " + tileType);
        }
    }
}
