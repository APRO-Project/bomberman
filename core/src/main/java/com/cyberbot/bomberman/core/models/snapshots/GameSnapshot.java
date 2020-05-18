package com.cyberbot.bomberman.core.models.snapshots;

import com.badlogic.gdx.math.Vector2;

import java.io.Serializable;

// TODO: Add all missing fields (entities, other players, tiles)
public class GameSnapshot implements Serializable {
    public final int sequence;
    public Vector2 position;

    public GameSnapshot(int sequence) {
        this.sequence = sequence;
    }

    @Override
    public String toString() {
        return "GameSnapshot{" +
            "position=" + position +
            '}';
    }
}
