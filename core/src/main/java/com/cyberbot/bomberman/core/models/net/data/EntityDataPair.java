package com.cyberbot.bomberman.core.models.net.data;

import com.cyberbot.bomberman.core.models.entities.Entity;

public class EntityDataPair {
    private final Entity entity;
    private final EntityData<?> data;

    public EntityDataPair(Entity entity, EntityData<?> data) {
        if (entity.getId() != data.getId()) {
            throw new IllegalArgumentException("Ids of entity and data d not match");
        }

        this.entity = entity;
        this.data = data;
    }

    public Entity getEntity() {
        return entity;
    }

    public EntityData<?> getData() {
        return data;
    }
}
