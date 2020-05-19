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

//    /**
//     * Factory method for creating new Tiles from a LibGDX {@link TiledMapTile TiledMapTile}.
//     *
//     * @param tile  The reference TiledMapTile.
//     * @param world Box2D world reference, required for any {@link PhysicalTile PhysicalTiles}.
//     * @param x     The x component of the tile's position.
//     * @param y     The y component of the tile's position.
//     * @return A new instance of a Tile.
//     * @throws InvalidPropertiesFormatException When some required properties where missing from the TiledMapTile
//     *                                          or were of an invalid type.
//     * @throws IllegalArgumentException         When a property contains an illegal value.
//     */
//    public static Tile createTile(TiledMapTile tile, World world, int x, int y)
//        throws InvalidPropertiesFormatException {
//        MapProperties properties = tile.getProperties();
//        if (!properties.containsKey(Tile.PROPERTY_TILE_TYPE) ||
//            !properties.containsKey(Tile.PROPERTY_TEXTURE_NAME)) {
//            throw new InvalidPropertiesFormatException(
//                "Each tile in the tile map has to contain '" +
//                    Tile.PROPERTY_TILE_TYPE + "' and '" +
//                    Tile.PROPERTY_TEXTURE_NAME + "' properties"
//            );
//        }
//
//        String tileType = properties.get(Tile.PROPERTY_TILE_TYPE, String.class);
//        String textureName = properties.get(Tile.PROPERTY_TEXTURE_NAME, String.class);
//
//        switch (tileType) {
//            case Tile.TILE_TYPE_FLOOR:
//                return new FloorTile(
//                    textureName,
//                    FloorTile.Properties.fromMapProperties(properties),
//                    x, y
//                );
//            case Tile.TILE_TYPE_WALL:
//                return new WallTile(world, textureName,
//                    WallTile.Properties.fromMapProperties(properties),
//                    x, y
//                );
//            case Tile.TILE_TYPE_BASE:
//                return new Tile(textureName, x, y);
//            default:
//                throw new IllegalArgumentException("Invalid tile type: " + tileType);
//        }
//    }

    public static com.cyberbot.bomberman.core.models.tiles.Tile createTile(Tile tile, World world, int x, int y) {
        if (tile == null){
            return null;
        }

        String tileType = "";
        String textureName = "";

        Properties properties = tile.getProperties();
        for (Property property : properties.getProperty()){
            if (property.getName().equals(PROPERTY_TILE_TYPE)){
                tileType = property.getValue();
            }
            if (property.getName().equals(PROPERTY_TEXTURE_NAME)){
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
