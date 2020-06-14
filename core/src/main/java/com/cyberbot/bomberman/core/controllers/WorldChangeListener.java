package com.cyberbot.bomberman.core.controllers;

import com.cyberbot.bomberman.core.models.entities.Entity;

/**
 * An interface that any parties interested in changes to the world entities should implement.
 */
public interface WorldChangeListener {
    /**
     * Called when a new {@link Entity} has been added to game.
     *
     * @param entity The new entity.
     */
    default void onEntityAdded(Entity entity) {

    }

    /**
     * Called when an {@link Entity} has been removed from the game.
     *
     * @param entity The removed entity.
     */
    default void onEntityRemoved(Entity entity) {

    }
}
