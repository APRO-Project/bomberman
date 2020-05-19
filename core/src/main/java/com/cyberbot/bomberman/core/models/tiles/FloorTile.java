package com.cyberbot.bomberman.core.models.tiles;

import com.cyberbot.bomberman.core.models.tiles.loader.tileset.Property;

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

        static Properties fromTileProperties(com.cyberbot.bomberman.core.models.tiles.loader.tileset.Properties properties) {
            float maxSpeedMultiplier = 1f;
            float dragMultiplier = 1f;

            for (Property property : properties.getProperty()) {
                if (property.getName().equals("max_speed")) {
                    maxSpeedMultiplier = Float.parseFloat(property.getValue());
                }

                if (property.getName().equals("drag")) {
                    dragMultiplier = Float.parseFloat(property.getValue());
                }
            }

            return new Properties(maxSpeedMultiplier, dragMultiplier);
        }
    }
}
