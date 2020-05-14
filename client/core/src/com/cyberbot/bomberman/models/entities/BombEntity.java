package com.cyberbot.bomberman.models.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

public class BombEntity extends Entity {

    private float detonationTime = 3;
    private float range = 3;
    private float power = 1;

    private boolean blown;

    public BombEntity(World world, String atlasPath) {
        super(world);

        sprite = new Sprite(new Texture("bomb.png"));
        blown = false;
    }

    public float getDetonationTime() {
        return detonationTime;
    }

    public float getRange() {
        return range;
    }

    public float getPower() {
        return power;
    }

    public void setDetonationTime(float detonationTime) {
        if(detonationTime < 0) throw new IllegalArgumentException("Detonation time must be < 0");

        this.detonationTime = detonationTime;
    }

    public void setRange(float range) {
        if(range < 0) throw new IllegalArgumentException("Bomb range must be < 0");

        this.range = range;
    }

    public void setPower(float power) {
        if(power <= 0) throw new IllegalArgumentException("Bomb power must be <= 0");

        this.power = power;
    }

    public boolean isBlown() {
        return blown;
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
        super.update(delta);

        detonationTime -= delta;
        Gdx.app.log("bomberman", "Bomb time left: " + detonationTime + "s");
        if(detonationTime <= 0) blown = true;
    }
}
