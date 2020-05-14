package com.cyberbot.bomberman.models.tiles;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import java.util.InvalidPropertiesFormatException;

public class FloorTile extends PhysicalTile {
    private final Properties properties;

    public FloorTile(World world, Vector2 position, Sprite texture, Properties properties) {
        super(world, position, texture);

        this.properties = properties;
        body.getFixtureList().forEach(f -> f.setSensor(true));
    }

    @Override
    protected void createFixture() {
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.5F, 0.5F);

        Fixture fixture = body.createFixture(shape, 1);
        fixture.setSensor(true);

        shape.dispose();
    }

    public Properties getProperties() {
        return properties;
    }

    static class Properties {
        static final String MAX_SPEED = "max_speed";
        static final String DRAG = "drag";

        float maxSpeedMultiplier;
        float dragMultiplier;

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
