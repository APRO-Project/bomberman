package com.cyberbot.bomberman.core.models.entities;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.cyberbot.bomberman.core.models.net.data.ExplosionData;

public class ExplosionEntity extends Entity {
    public static float DO_NOT_DECAY = -1;
    private float decayTimeLeft;
    private boolean decayed;

    public ExplosionEntity(World world, long id, float decayTime) {
        super(world, id);

        this.decayed = false;
        this.decayTimeLeft = decayTime;
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

        decayTimeLeft = Math.max(0, decayTimeLeft - delta);
        if (decayTimeLeft == 0) {
            decayed = true;
        }
    }

    @Override
    public ExplosionData getData() {
        return new ExplosionData(id, getPosition());
    }

    public boolean isDecayed() {
        return decayed;
    }
}
