package com.cyberbot.bomberman.models.entities;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.cyberbot.bomberman.models.Updatable;
import com.cyberbot.bomberman.models.defs.BombDef;

public class BombEntity extends Entity implements Updatable {
    private final BombDef def;

    private float timeLeft;
    private boolean blown;

    public BombEntity(World world, BombDef def) {
        super(world);
        this.def = def;

        this.timeLeft = def.detonationTime;
        this.blown = false;
    }

    @Override
    public void createBody(World world) {
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.StaticBody;
        def.position.set(0, 0);
        def.fixedRotation = true;

        body = world.createBody(def);

        CircleShape shape = new CircleShape();
        shape.setRadius(0.45f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;

        body.createFixture(fixtureDef);
        body.setUserData(this);
        shape.dispose();
    }

    @Override
    public void update(float delta) {
        if(timeLeft > 0) {
            timeLeft = Math.max(timeLeft - delta, 0);
        }

        if(!blown && timeLeft == 0) {
            blown = true;
        }
    }

    public float getRange() {
        return def.range;
    }

    public float getPower() {
        return def.power;
    }

    public float getLeftFraction() {
        return timeLeft / def.detonationTime;
    }

    public boolean isBlown() {
        return blown;
    }
}
