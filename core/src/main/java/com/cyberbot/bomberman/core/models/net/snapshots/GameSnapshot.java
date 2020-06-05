package com.cyberbot.bomberman.core.models.net.snapshots;

import com.cyberbot.bomberman.core.models.net.data.EntityData;

import java.io.Serializable;
import java.util.Map;

// TODO: Add tiles
public class GameSnapshot implements Serializable {
    private final Map<Long, EntityData<?>> entities;

    public GameSnapshot(Map<Long, EntityData<?>> entities) {
        this.entities = entities;
    }

    public boolean hasEntity(long id) {
        return entities.containsKey(id);
    }

    public EntityData<?> getEntity(long id) {
        return entities.get(id);
    }

    public Map<Long, EntityData<?>> getEntities() {
        return entities;
    }
}
