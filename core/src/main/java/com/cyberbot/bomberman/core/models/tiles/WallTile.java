package com.cyberbot.bomberman.core.models.tiles;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import java.util.InvalidPropertiesFormatException;

/**
 * Objects of this class represent a wall tile with special {@link WallTile.Properties}.
 * It is a PhysicalTile meaning it has a Box2D body associated with it.
 * The tile's hit box is a 1x1 square in the Box2D world.
 */
public class WallTile extends PhysicalTile {
    private final Properties properties;

    public WallTile(World world, String textureName, Properties properties, int x, int y) {
        super(world, textureName, x, y);
        this.properties = properties;
    }

    @Override
    protected void createFixture() {
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.5f, 0.5f);

        body.createFixture(shape, 1);

        shape.dispose();
    }

    public Properties getProperties() {
        return properties;
    }

    /**
     * Properties of the wall tile type.
     */
    public static class Properties {
        public static final float DURABILITY_INFINITE = -1;
        public static final float POWER_DROPOFF = 0.5f; // TODO: Move the property to the bomb entity

        static final String DURABILITY = "durability";

        /**
         * Determines the durability of this wall type.
         * When set to {@link #DURABILITY_INFINITE} the tile is indestructible.
         */
        public final float durability;

        Properties(float durability) {
            this.durability = durability;
        }

        /**
         * Factory method for creating the properties object from given LibGDX MapProperties.
         *
         * @param properties Properties source.
         * @return A new instance of the Properties object.
         * @throws InvalidPropertiesFormatException When some required properties where missing or
         *                                          of invalid type from the MapProperties.
         */
        static Properties fromMapProperties(MapProperties properties) throws InvalidPropertiesFormatException {
            try {
                return new Properties(
                    properties.get(DURABILITY, DURABILITY_INFINITE, float.class)
                );
            } catch (ClassCastException e) {
                throw new InvalidPropertiesFormatException("The type of '" + DURABILITY + "' property has to be float.");
            }
        }
    }
}