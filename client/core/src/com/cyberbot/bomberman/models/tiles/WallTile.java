package com.cyberbot.bomberman.models.tiles;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class WallTile extends PhysicalTile {
    public WallTile(World world, Vector2 position, Sprite sprite) {
        super(world, position, sprite);
    }

    @Override
    protected void createFixture() {
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.5F, 0.5F);

        body.createFixture(shape, 1);

        shape.dispose();
    }
}
