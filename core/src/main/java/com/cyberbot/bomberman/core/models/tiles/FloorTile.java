package com.cyberbot.bomberman.core.models.tiles;


import java.util.HashMap;
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

        static Properties fromProperties(HashMap<String, Object> properties) throws InvalidPropertiesFormatException {
            if (!properties.containsKey(DRAG) ||
                !properties.containsKey(MAX_SPEED)) {
                throw new InvalidPropertiesFormatException(
                    "Floor tiles have to contain '" +
                        FloorTile.Properties.DRAG + "' and '" +
                        FloorTile.Properties.MAX_SPEED + "' float properties"
                );
            }

            try {
                return new Properties(
                    (float) properties.get(MAX_SPEED),
                    (float) properties.get(DRAG)
                );
            } catch (ClassCastException e) {
                throw new InvalidPropertiesFormatException("Floor tiles have to contain '" +
                    FloorTile.Properties.DRAG + "' and '" +
                    FloorTile.Properties.MAX_SPEED + "' float properties");
            }
        }
    }
}
