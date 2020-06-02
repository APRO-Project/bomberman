package com.cyberbot.bomberman.core.models.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.cyberbot.bomberman.core.models.Updatable;
import com.cyberbot.bomberman.core.models.net.data.EntityData;
import com.cyberbot.bomberman.core.utils.Constants;

import static com.cyberbot.bomberman.core.utils.Constants.PPM;

/**
 * Abstract base for all game entities that contain a Box2D body.
 */
public abstract class Entity implements Disposable, Updatable {
    private boolean remove;

    protected final long id;
    protected Body body;

    public Entity(World world, long id) {
        this.id = id;
        this.remove = false;
        createBody(world);
    }

    /**
     * Implementations of this method should create a Box2D body.
     *
     * @param world The world to bind the body to.
     */
    public abstract void createBody(World world);

    @Override
    public void update(float delta) {
        if (remove) {
            dispose();
        }
    }

    /**
     * Destroys the entity's body.
     *
     * @see World#destroyBody(Body)
     */
    @Override
    public void dispose() {
        body.getWorld().destroyBody(body);
    }

    /**
     * Sets the position of the entity in the pixel coordinate system
     *
     * @see Constants#PPM
     */
    public void setPosition(Vector2 position) {
        body.setTransform(position.x / PPM, position.y / PPM, 0);
    }

    /**
     * Returns the position of the entity in the pixel coordinate system
     *
     * @return the position of the entity in the pixel coordinate system
     * @see Constants#PPM
     */
    public Vector2 getPosition() {
        Vector2 position = body.getPosition();
        return new Vector2(position.x * PPM, position.y * PPM);
    }

    /**
     * Returns the position of the entity in the Box2D coordinate system.
     *
     * @return the position of the entity in the Box2D coordinate system.
     */
    public Vector2 getPositionRaw() {
        return body.getPosition();
    }

    /**
     * Sets the position of the entity in the Box2D coordinate system.
     */
    public void setPositionRaw(Vector2 position) {
        body.setTransform(position, 0);
    }

    public boolean isMarkedToRemove() {
        return remove;
    }

    /**
     * Marks the entity as removed.
     * The entity will be disposed in the next {@link #update(float) update} call.
     *
     * @see #dispose()
     */
    public void markToRemove() {
        this.remove = true;
    }

    public long getId() {
        return id;
    }

    public void updateFromData(EntityData<?> data) {
        if (data.getId() != id) {
            throw new IllegalArgumentException("Provided data is not meant for this entity, ids do not match");
        }

        setPosition(data.getPosition().toVector2());
    }

    public void updateFromData(EntityData<?> d0, EntityData<?> d1, float fraction) {
        if (d0.getId() != id || d1.getId() != id) {
            throw new IllegalArgumentException("Provided data is not meant for this entity, ids do not match");
        }

        if (fraction > 1 || fraction < 0) {
            throw new IllegalArgumentException("Interpolation fraction has to be in range 0-1");
        }

        final Vector2 pos0 = d0.getPosition().toVector2();
        final Vector2 pos1 = d1.getPosition().toVector2();

        // After this operation pos1 holds the delta vector and pos0 the new resulting position
        pos0.mulAdd(pos1.sub(pos0), fraction);

        setPosition(pos0);
    }

    public abstract EntityData<? extends Entity> getData();
}
