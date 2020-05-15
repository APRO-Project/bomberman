package com.cyberbot.bomberman.models.tiles;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;

import java.util.InvalidPropertiesFormatException;

public class FloorTile extends Tile {
    private final Properties properties;

    public FloorTile(String textureName, Vector2 position, Properties properties) {
        super(textureName, position);
        this.properties = properties;
    }

    public Properties getProperties() {
        return properties;
    }

    public static class Properties {
        static final String MAX_SPEED = "max_speed";
        static final String DRAG = "drag";

        public final float maxSpeedMultiplier;
        public final float dragMultiplier;

        public Properties(float maxSpeedMultiplier, float dragMultiplier) {
            this.maxSpeedMultiplier = maxSpeedMultiplier;
            this.dragMultiplier = dragMultiplier;
        }

        static Properties fromMapProperties(MapProperties properties) throws InvalidPropertiesFormatException {
            if (!properties.containsKey(DRAG) ||
                    !properties.containsKey(MAX_SPEED)) {
                throw new InvalidPropertiesFormatException(
                        "Floor tiles have to contain '" +
                                FloorTile.Properties.DRAG + "' and '" +
                                FloorTile.Properties.MAX_SPEED + "' properties"
                );
            }

            return new Properties(
                    properties.get(MAX_SPEED, float.class),
                    properties.get(DRAG, float.class)
            );
        }
    }
}
