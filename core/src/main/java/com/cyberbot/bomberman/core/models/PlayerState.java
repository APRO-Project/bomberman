package com.cyberbot.bomberman.core.models;

import com.badlogic.gdx.math.Vector2;

public class PlayerState {
    /**
     * The snapshot's sequence that this state describes
     */
    public final int sequence;
    public final Vector2 position;
    public final Vector2 velocity;

    public PlayerState(int sequence, Vector2 position, Vector2 velocity) {
        this.sequence = sequence;
        this.position = position;
        this.velocity = velocity;
    }
}
