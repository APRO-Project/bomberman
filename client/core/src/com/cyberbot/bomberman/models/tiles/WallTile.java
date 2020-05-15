package com.cyberbot.bomberman.models.tiles;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class WallTile extends PhysicalTile {
    private final Properties properties;

    public WallTile(String textureName, Vector2 position, Properties properties, World world) {
        super(textureName, position, world);
        this.properties = properties;
    }

    @Override
    protected void createFixture() {
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.5F, 0.5F);

        body.createFixture(shape, 1);

        shape.dispose();
    }

    public Properties getProperties() {
        return properties;
    }

    public static class Properties {
        static final String DURABILITY = "durability";

        final float durability;

        Properties(float durability) {
            this.durability = durability;
        }

        static Properties fromMapProperties(MapProperties properties) {
            return new Properties(
                    properties.get(DURABILITY, -1F, float.class)
            );
        }
    }
}
