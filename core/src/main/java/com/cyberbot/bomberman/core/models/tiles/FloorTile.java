package com.cyberbot.bomberman.core.models.tiles;

import com.cyberbot.bomberman.core.models.tiles.loader.tileset.Property;

import java.util.InvalidPropertiesFormatException;

/**
 * Objects of this class represent a floor tile with special {@link Properties}.
 */
public class FloorTile extends Tile {
    private final Properties properties;

    public FloorTile(String textureName, Properties properties, int x, int y) {
        super(textureName, x, y);
        this.properties = properties;
    }

    public Properties getProperties() {
        return properties;
    }

    /**
     * Properties of the floor tile type.
     */
    public static class Properties {
        static final String MAX_SPEED = "max_speed";
        static final String DRAG = "drag";

        /**
         * A multiplier that determines the maximum speed a player can reach on this floor type.
         * Values greater then 1 denote a greater speed and values lower then 1 denote a lower speed.
         */
        public final float maxSpeedMultiplier;

        /**
         * A multiplier that determines the drag a tile excerpts on the player.
         * Values greater then 1 denote faster acceleration and deceleration
         * and lower then 1 denote slower acceleration and deceleration.
         */
        public final float dragMultiplier;

        public Properties(float maxSpeedMultiplier, float dragMultiplier) {
            this.maxSpeedMultiplier = maxSpeedMultiplier;
            this.dragMultiplier = dragMultiplier;
        }

//        /**
//         * Factory method for creating the properties object from given LibGDX MapProperties.
//         *
//         * @param properties Properties source.
//         * @return A new instance of the Properties object.
//         * @throws InvalidPropertiesFormatException When some required properties where missing or
//         *                                          of invalid type from the MapProperties.
//         */
//        static Properties fromMapProperties(MapProperties properties) throws InvalidPropertiesFormatException {
//            if (!properties.containsKey(DRAG) ||
//                !properties.containsKey(MAX_SPEED)) {
//                throw new InvalidPropertiesFormatException(
//                    "Floor tiles have to contain '" +
//                        FloorTile.Properties.DRAG + "' and '" +
//                        FloorTile.Properties.MAX_SPEED + "' float properties"
//                );
//            }
//
//            try {
//                return new Properties(
//                    properties.get(MAX_SPEED, float.class),
//                    properties.get(DRAG, float.class)
//                );
//            } catch (ClassCastException e) {
//                throw new InvalidPropertiesFormatException("Floor tiles have to contain '" +
//                    FloorTile.Properties.DRAG + "' and '" +
//                    FloorTile.Properties.MAX_SPEED + "' float properties");
//            }
//        }

        static Properties fromTileProperties(com.cyberbot.bomberman.core.models.tiles.loader.tileset.Properties properties) {
            float maxSpeedMultiplier = 1f;
            float dragMultiplier = 1f;

            for (Property property : properties.getProperty()){
                if (property.getName().equals("max_speed")){
                    maxSpeedMultiplier = Float.parseFloat(property.getValue());
                }

                if (property.getName().equals("drag")){
                    dragMultiplier = Float.parseFloat(property.getValue());
                }
            }

            return new Properties(maxSpeedMultiplier, dragMultiplier);
        }
    }
}
