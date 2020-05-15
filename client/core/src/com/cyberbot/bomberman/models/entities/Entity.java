package com.cyberbot.bomberman.models.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;

import static com.cyberbot.bomberman.utils.Constants.PPM;

public abstract class Entity implements Disposable {
    protected Body body;

    public Entity(World world) {
        createBody(world);
    }

    public abstract void createBody(World world);

    @Override
    public void dispose() {
        body.getWorld().destroyBody(body);
    }

    public void setPosition(Vector2 position) {
        body.setTransform(position.x / PPM, position.y / PPM, 0);
    }

    public Vector2 getPosition() {
        Vector2 position = body.getPosition();
        return new Vector2(position.x * PPM, position.y * PPM);
    }

    public Vector2 getPositionRaw() {
        return body.getPosition();
    }

}
