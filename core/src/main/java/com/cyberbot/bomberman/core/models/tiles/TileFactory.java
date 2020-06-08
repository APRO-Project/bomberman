package com.cyberbot.bomberman.core.models.tiles;

import com.badlogic.gdx.physics.box2d.World;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;

/**
 * Factory for the {@link Tile Tile} objects.
 */
public class TileFactory {


    private static final String PROPERTY_TILE_TYPE = "tile_type";
    private static final String PROPERTY_TEXTURE_NAME = "texture";

    private static final String TILE_TYPE_FLOOR = "floor";
    private static final String TILE_TYPE_WALL = "wall";
    private static final String TILE_TYPE_BASE = "base";

    /**
     * Factory method for parsing DOM NodeList of tile properties and creating a new tile based on them.
     *
     * @param tile DOM NodeList of tile properties.
     * @param world The world to bind the body to.
     * @param x horizontal coordinate.
     * @param y vertical coordinate.
     * @return created Tile
     * @throws InvalidPropertiesFormatException thrown on misdefined value type.
     */
    public static Tile createTile(NodeList tile, World world, int x, int y) throws InvalidPropertiesFormatException {
        if (tile == null) {
            return null;
        }

        HashMap<String, Object> properties = new HashMap<>();

        for (int i = 0; i < tile.getLength(); i++) {
            Element element = (Element) tile.item(i);
            switch (element.getAttribute("type")) {
                case "float":
                    properties.put(element.getAttribute("name"),
                        Float.parseFloat(element.getAttribute("value")));
                    break;
                case "int":
                    properties.put(element.getAttribute("name"),
                        Integer.parseInt(element.getAttribute("value")));
                    break;
                default:
                    properties.put(element.getAttribute("name"), element.getAttribute("value"));
            }
        }

        if (!properties.containsKey(PROPERTY_TILE_TYPE) || !properties.containsKey(PROPERTY_TEXTURE_NAME)) {
            throw new InvalidPropertiesFormatException(
                "Each tile in the tile map has to contain '" +
                    PROPERTY_TILE_TYPE + "' and '" +
                    PROPERTY_TEXTURE_NAME + "' properties"
            );
        }

        String tileType;
        String textureName;

        try {
            tileType = (String) properties.get(PROPERTY_TILE_TYPE);
            textureName = (String) properties.get(PROPERTY_TEXTURE_NAME);
        } catch (ClassCastException classCastException) {
            throw new InvalidPropertiesFormatException("All tiles have to contain '" +
                PROPERTY_TILE_TYPE + "' and '" +
                PROPERTY_TEXTURE_NAME + "' String(default) properties");
        }

        switch (tileType) {
            case TILE_TYPE_FLOOR:
                return new FloorTile(
                    textureName,
                    FloorTile.Properties.fromProperties(properties),
                    x, y
                );
            case TILE_TYPE_WALL:
                return new WallTile(world, textureName,
                    WallTile.Properties.fromProperties(properties),
                    x, y
                );
            case TILE_TYPE_BASE:
                return new Tile(textureName, x, y);
            default:
                throw new IllegalArgumentException("Invalid tile type: " + tileType);
        }
    }
}
