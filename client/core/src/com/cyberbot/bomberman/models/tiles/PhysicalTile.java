package com.cyberbot.bomberman.models.tiles;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;

public abstract class PhysicalTile extends BaseTile {
    protected Body body;
    private World world;

    public PhysicalTile(World world, Vector2 position, Sprite sprite) {
        super(sprite, position);
        this.world = world;

        position.add(0.5F, 0.5F);
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.StaticBody;
        def.position.set(position);
        def.fixedRotation = true;

        body = world.createBody(def);

        createFixture();
    }

    protected abstract void createFixture();

    @Override
    public void dispose() {
        world.destroyBody(body);
        sprite.getTexture().dispose();
    }
}
