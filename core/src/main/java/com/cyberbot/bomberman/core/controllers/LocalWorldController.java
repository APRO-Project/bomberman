package com.cyberbot.bomberman.core.controllers;

import com.badlogic.gdx.physics.box2d.World;
import com.cyberbot.bomberman.core.models.Updatable;
import com.cyberbot.bomberman.core.models.entities.Entity;
import com.cyberbot.bomberman.core.models.entities.PlayerEntity;
import com.cyberbot.bomberman.core.models.net.EntityData;
import com.cyberbot.bomberman.core.models.net.EntityDataPair;
import com.cyberbot.bomberman.core.models.snapshots.GameSnapshot;

import java.util.*;
import java.util.stream.Collectors;

public class LocalWorldController implements Updatable {
    private World world;
    private HashMap<Long, Entity> entities;
    private Queue<GameSnapshot> gameSnapshots;
    private SnapshotQueue interactionQueue;
    private PlayerEntity localPlayer;
    private Queue<EntityData<?>> updateQueue;

    @Override
    public void update(float delta) {
        world.step(delta, 6, 2);

        // Update all entities
        entities.forEach((key, value) -> value.update(delta));

        // Remove any entities marked for removal
        entities.entrySet().removeIf(it -> it.getValue().isMarkedToRemove());
    }

    /**
     * Requires the Box2D world to not be locked, so it has to be called during an update
     * and not asynchronously
     *
     * @param snapshot Game snapshot to rollback the World to
     */
    private void rollback(GameSnapshot snapshot) {
        if (world.isLocked()) {
            throw new ConcurrentModificationException("Cannot rollback while world is locked");
        }

        List<Entity> removed = getRemovedEntities(snapshot);
        List<EntityData<?>> added = getAddedEntityData(snapshot);
        List<EntityDataPair> updated = getUpdatedEntities(snapshot);

        List<Long> removedIds = removed.stream().map(Entity::getId).collect(Collectors.toList());

        removed.forEach(Entity::dispose);
        entities.keySet().removeAll(removedIds);

        updated.forEach(it -> it.getEntity().updateFromData(it.getData()));
        List<Entity> createdEntities = added.stream()
            .map(it -> it.createEntity(world))
            .collect(Collectors.toList());

        createdEntities.forEach(it -> entities.put(it.getId(), it));
    }

    private List<Entity> getRemovedEntities(GameSnapshot snapshot) {
        return entities.entrySet().stream()
            .filter(it -> !snapshot.hasEntity(it.getKey()))
            .map(Map.Entry::getValue)
            .collect(Collectors.toList());
    }

    private List<EntityData<?>> getAddedEntityData(GameSnapshot snapshot) {
        return snapshot.entities.stream()
            .filter(it -> !hasEntity(it.getId()))
            .collect(Collectors.toList());
    }

    private List<EntityDataPair> getUpdatedEntities(GameSnapshot snapshot) {
        return snapshot.entities.stream()
            .filter(it -> hasEntity(it.getId()))
            .map(it -> new EntityDataPair(entities.get(it.getId()), it))
            .collect(Collectors.toList());
    }

    private boolean hasEntity(long id) {
        return entities.containsKey(id);
    }
}
