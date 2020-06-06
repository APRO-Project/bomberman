package com.cyberbot.bomberman.core.models;

import com.badlogic.gdx.math.Vector2;

public class PlayerState {
    public PlayerState(int sequence, Vector2 position, Vector2 velocity) {
        this.sequence = sequence;
        this.position = position;
        this.velocity = velocity;
    }

    public int sequence;
    public Vector2 position;
    public Vector2 velocity;
}
