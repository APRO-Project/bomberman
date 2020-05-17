package com.cyberbot.bomberman.core.models;

/**
 * Interface for objects that should update when time passes.
 */
public interface Updatable {
    /**
     * Called when an object should update itself.
     *
     * @param delta The time in seconds since the last update.
     */
    void update(float delta);
}
