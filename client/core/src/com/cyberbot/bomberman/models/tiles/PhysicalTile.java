package com.cyberbot.bomberman.models.tiles;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import static com.cyberbot.bomberman.utils.Constants.PPM;

public abstract class PhysicalTile extends BaseTile {
    protected Body body;

    public PhysicalTile(World world, Vector2 position, Sprite sprite) {
        super(sprite);

        sprite.setPosition(position.x * PPM, position.y * PPM);

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
        sprite.getTexture().dispose();
    }
}
