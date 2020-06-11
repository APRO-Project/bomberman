package com.cyberbot.bomberman.core.models.entities;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.cyberbot.bomberman.core.models.defs.BombDef;
import com.cyberbot.bomberman.core.models.items.ItemType;
import com.cyberbot.bomberman.core.models.net.data.BombData;

/**
 * A bomb entity.
 */
public class BombEntity extends Entity {
    private final float power;
    private final float powerDropoff;
    private final float range;
    private final float detonationTime;
    private final int playerTextureVariant;
    private final ItemType bombItemType;

    private float timeLeft;
    private boolean blown;

    public BombEntity(World world, BombDef def, long id) {
        super(world, id);

        if (!def.bombItemType.isBomb()) {
            throw new IllegalArgumentException("Item is not of bomb type: " + def.bombItemType);
        }

        this.power = def.power;
        this.range = def.range;
        this.detonationTime = def.detonationTime;
        this.timeLeft = def.detonationTime;
        this.playerTextureVariant = def.playerTextureVariant;
        this.powerDropoff = def.powerDropOff;
        this.bombItemType = def.bombItemType;

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

    /**
     * Decreases the time left on the bomb, and sets it to blown if the time reaches 0.
     *
     * @param delta The time in seconds since the last update.
     */
    @Override
    public void update(float delta) {
        super.update(delta);
        if (timeLeft > 0) {
            timeLeft = Math.max(timeLeft - delta, 0);
        }

        if (!blown && timeLeft == 0) {
            blown = true;
        }
    }

    @Override
    public BombData getData() {
        return new BombData(id, getPosition(), playerTextureVariant, bombItemType);
    }

    public float getRange() {
        return range;
    }

    public float getPower() {
        return power;
    }

    public float getLeftFraction() {
        return timeLeft / detonationTime;
    }

    public boolean isBlown() {
        return blown;
    }

    public int getPlayerTextureVariant() {
        return playerTextureVariant;
    }

    public float getPowerDropoff() {
        return powerDropoff;
    }

    public ItemType getBombItemType() {
        return bombItemType;
    }
}
