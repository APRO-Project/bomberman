package com.cyberbot.bomberman.core.models.tiles;

import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.cyberbot.bomberman.core.models.net.data.WallTileData;

import java.io.Serializable;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;

/**
 * Objects of this class represent a wall tile with special {@link WallTile.Properties}.
 * It is a PhysicalTile meaning it has a Box2D body associated with it.
 * The tile's hit box is a 1x1 square in the Box2D world.
 */
public class WallTile extends PhysicalTile {
    private final Properties properties;
    private float durability;

    public WallTile(World world, String textureName, Properties properties, int x, int y) {
        super(world, textureName, x, y);
        this.properties = properties;
        this.durability = properties.durability;
    }

    @Override
    protected void createFixture() {
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.5f, 0.5f);

        body.createFixture(shape, 1);

        shape.dispose();
    }

    @Override
    public WallTileData getData() {
        return new WallTileData(x, y, textureName, properties);
    }

    public float subtractDurability(float power) {
        if (durability == Properties.DURABILITY_INFINITE) {
            return 0;
        }

        float remainingPower = Math.max(0, power - durability);
        durability = Math.max(0, durability - power);
        return remainingPower;
    }

    public boolean isDestroyed() {
        return durability == 0;
    }

    /**
     * Properties of the wall tile type.
     */
    public static class Properties implements Serializable {
        public static final float DURABILITY_INFINITE = -1;

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
         */

        static Properties fromProperties(HashMap<String, Object> properties) throws InvalidPropertiesFormatException {
            float durability = DURABILITY_INFINITE;
            try {
                if (properties.containsKey(DURABILITY)) {
                    durability = (float) properties.get(DURABILITY);
                }
                return new Properties(durability);
            } catch (ClassCastException e) {
                throw new InvalidPropertiesFormatException("Wall tiles have to contain '" +
                    Properties.DURABILITY + "' float property");
            }
        }

    }
}
