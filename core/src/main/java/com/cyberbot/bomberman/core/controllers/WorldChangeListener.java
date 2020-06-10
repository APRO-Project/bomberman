package com.cyberbot.bomberman.core.controllers;

import com.cyberbot.bomberman.core.models.entities.Entity;
import com.cyberbot.bomberman.core.models.entities.PlayerEntity;

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
     * <p>
     * Note: This will not be called when a player dies unless it is also removed from the game.
     *
     * @param entity The removed entity.
     * @see #onPlayerDied(PlayerEntity)
     */
    default void onEntityRemoved(Entity entity) {

    }

    /**
     * Called when a {@link PlayerEntity} has died.
     * Calls {@link #onEntityRemoved(Entity)} with the given player by default.
     *
     * @param playerEntity The player that died entity.
     * @see #onEntityRemoved(Entity)
     */
    default void onPlayerDied(PlayerEntity playerEntity) {
        onEntityRemoved(playerEntity);
    }
}
