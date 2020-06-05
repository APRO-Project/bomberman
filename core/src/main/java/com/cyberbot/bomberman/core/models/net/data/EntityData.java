package com.cyberbot.bomberman.core.models.net.data;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.cyberbot.bomberman.core.models.entities.Entity;

import java.io.Serializable;

/**
 * This is a lightweight POJO which handles the serialization and deserialization of entities.
 * Each entity has to implement {@link Entity#getData()} which returns the serializable instance of this class,
 * which in turn has to implement {@link EntityData#createEntity(World)} which creates an entity from the data.
 *
 * @param <E> The Entity that this Data is associated with.
 */
public abstract class EntityData<E extends Entity> implements Serializable {
    protected final long id;
    protected final VectorData position;

    public EntityData(long id, Vector2 position) {
        this.id = id;
        this.position = new VectorData(position.x, position.y);
    }

    public abstract E createEntity(World world);

    public VectorData getPosition() {
        return position;
    }

    public long getId() {
        return id;
    }
}
