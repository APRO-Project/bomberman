package com.cyberbot.bomberman.core.controllers;

import com.badlogic.gdx.physics.box2d.World;
import com.cyberbot.bomberman.core.models.Updatable;
import com.cyberbot.bomberman.core.models.entities.Entity;
import com.cyberbot.bomberman.core.models.entities.PlayerEntity;
import com.cyberbot.bomberman.core.models.net.EntityData;
import com.cyberbot.bomberman.core.models.net.EntityDataPair;
import com.cyberbot.bomberman.core.models.snapshots.GameSnapshot;
import com.cyberbot.bomberman.core.utils.Utils;

import java.util.*;
import java.util.stream.Collectors;

public class LocalWorldController implements Updatable {
    private final World world;
    private final Map<Long, Entity> entities;
    private final Queue<GameSnapshot> gameSnapshots;
    private final SnapshotQueue interactionQueue;
    private final PlayerEntity localPlayer;
    private final List<WorldChangeListener> listeners;

    public LocalWorldController(World world, SnapshotQueue interactionQueue, PlayerEntity localPlayer) {
        this.world = world;
        this.interactionQueue = interactionQueue;
        this.localPlayer = localPlayer;
        this.entities = new HashMap<>();
        this.gameSnapshots = new ArrayDeque<>();
        this.listeners = new ArrayList<>();
    }

    @Override
    public void update(float delta) {
        world.step(delta, 6, 2);

        // Update all entities
        entities.forEach((key, value) -> value.update(delta));

        // Remove any entities marked for removal
        entities.entrySet().removeIf(it -> it.getValue().isMarkedToRemove());
    }

    public void addListener(WorldChangeListener listener) {
        listeners.add(listener);
    }

    public void removeListener(WorldChangeListener listener) {
        listeners.remove(listener);
    }

    public void clearListeners() {
        listeners.clear();
    }

    /**
     * Applies a given snapshot to the current Box2D world by adding, updating and removing entities as needed.
     * <p>
     * Requires the Box2D world to not be locked, so it has to be called during an update
     * and not asynchronously.
     * <p>
     * When including the local player an effective rollback of the whole world is applied.
     * This requires the local player to be present in the snapshot, or a {@link NoSuchElementException}
     * will be thrown at runtime. The caller has to validate that the player is present in the snapshot.
     * <p>
     * Usually this method will be used if there is too much discrepancy
     * between the local player's position at the time of this snapshot and the snapshot,
     * meaning the local player had to be present in the snapshot anyway.
     *
     * @param snapshot           Game snapshot to rollback the World to
     * @param includeLocalPlayer Whether to apply the snapshot to the current player as well
     * @throws ConcurrentModificationException When the Box2D world is locked.
     * @throws NoSuchElementException          When the local player is not present in the snapshot's entity list.
     */
    private void applySnapshot(GameSnapshot snapshot, boolean includeLocalPlayer) {
        if (world.isLocked()) {
            throw new ConcurrentModificationException("Cannot rollback while world is locked");
        }

        List<Entity> removed = getRemovedEntities(snapshot);
        List<EntityData<?>> added = getAddedEntityData(snapshot);
        List<EntityDataPair> updated = getUpdatedEntities(snapshot);

        List<Long> removedIds = removed.stream().map(Entity::getId).collect(Collectors.toList());

        // Dispose and remove references to all removed entities
        removed.forEach(Entity::dispose);
        entities.keySet().removeAll(removedIds);

        // Update all updated entities
        updated.forEach(it -> it.getEntity().updateFromData(it.getData()));
        if (includeLocalPlayer) {
            EntityData<?> playerData = Utils.first(snapshot.entities, it -> it.getId() == localPlayer.getId());
            localPlayer.updateFromData(playerData);
        }

        // Create and add references to all added entities
        List<Entity> createdEntities = added.stream()
            .map(it -> it.createEntity(world))
            .collect(Collectors.toList());

        createdEntities.forEach(it -> entities.put(it.getId(), it));

        // Notify any listeners about changes
        listeners.forEach(l -> removed.forEach(l::onEntityRemoved));
        listeners.forEach(l -> createdEntities.forEach(l::onEntityAdded));
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
            .filter(it -> (hasEntity(it.getId())))
            .map(it -> new EntityDataPair(entities.get(it.getId()), it))
            .collect(Collectors.toList());
    }

    private boolean hasEntity(long id) {
        return entities.containsKey(id);
    }
}
