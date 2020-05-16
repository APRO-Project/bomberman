package com.cyberbot.bomberman.core.models.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.cyberbot.bomberman.core.models.Updatable;

import static com.cyberbot.bomberman.core.utils.Constants.PPM;

public abstract class Entity implements Disposable, Updatable {
    protected Body body;
    private boolean remove;

    public Entity(World world) {
        remove = false;
        createBody(world);
    }

    public abstract void createBody(World world);

    @Override
    public void update(float delta) {
        if (remove) {
            dispose();
        }
    }

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

    public void setPositionRaw(Vector2 position) {
        body.setTransform(position, 0);
    }

    public boolean isMarkedToRemove() {
        return remove;
    }

    public void markToRemove() {
        this.remove = true;
    }
}
