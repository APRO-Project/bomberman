package com.cyberbot.bomberman.core.models.entities;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.cyberbot.bomberman.core.models.net.data.ExplosionData;

public class ExplosionEntity extends Entity {

    private static final float DECAY_TIME = 1.5f;
    private float decayTimeLeft;

    public ExplosionEntity(World world, long id) {
        super(world, id);
        decayTimeLeft = 0;
    }

    @Override
    public void createBody(World world) {
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.StaticBody;
        def.position.set(0, 0);
        def.fixedRotation = true;

        body = world.createBody(def);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.5f, 0.5f);

        Fixture fixture = body.createFixture(shape, 1);
        fixture.setSensor(true);
        body.setUserData(this);
        shape.dispose();
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        decayTimeLeft += delta;
        if (decayTimeLeft >= DECAY_TIME) {
            markToRemove();
        }
    }

    @Override
    public ExplosionData getData() {
        return new ExplosionData(id, getPosition());
    }
}
