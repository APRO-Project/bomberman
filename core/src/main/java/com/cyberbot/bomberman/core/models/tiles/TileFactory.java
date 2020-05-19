package com.cyberbot.bomberman.core.models.tiles;

import com.badlogic.gdx.physics.box2d.World;
import com.cyberbot.bomberman.core.models.tiles.loader.tileset.Properties;
import com.cyberbot.bomberman.core.models.tiles.loader.tileset.Property;
import com.cyberbot.bomberman.core.models.tiles.loader.tileset.Tile;

/**
 * Factory for the {@link Tile Tile} objects.
 */
public class TileFactory {


    private static final String PROPERTY_TILE_TYPE = "tile_type";
    private static final String PROPERTY_TEXTURE_NAME = "texture";

    private static final String TILE_TYPE_FLOOR = "floor";
    private static final String TILE_TYPE_WALL = "wall";
    private static final String TILE_TYPE_BASE = "base";

    public static com.cyberbot.bomberman.core.models.tiles.Tile createTile(Tile tile, World world, int x, int y) {
        if (tile == null) {
            return null;
        }

        String tileType = "";
        String textureName = "";

        Properties properties = tile.getProperties();
        for (Property property : properties.getProperty()) {
            if (property.getName().equals(PROPERTY_TILE_TYPE)) {
                tileType = property.getValue();
            }
            if (property.getName().equals(PROPERTY_TEXTURE_NAME)) {
                textureName = property.getValue();
            }
        }

        switch (tileType) {
            case TILE_TYPE_FLOOR:
                return new FloorTile(
                    textureName,
                    FloorTile.Properties.fromTileProperties(properties),
                    x, y
                );
            case TILE_TYPE_WALL:
                return new WallTile(world, textureName,
                    WallTile.Properties.fromTileProperties(properties),
                    x, y
                );
            case TILE_TYPE_BASE:
                return new com.cyberbot.bomberman.core.models.tiles.Tile(textureName, x, y);
            default:
                throw new IllegalArgumentException("Invalid tile type: " + tileType);
        }
    }
}
