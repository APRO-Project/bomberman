package com.cyberbot.bomberman.core.models.snapshots;

import com.cyberbot.bomberman.core.models.actions.Action;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public final class PlayerSnapshot implements Serializable {
    public final int sequence;

    public int movingDirection;
    public final List<Action> actions;

    public PlayerSnapshot(int sequence) {
        this.sequence = sequence;
        movingDirection = 0;
        actions = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "PlayerSnapshot{" +
            "sequence=" + sequence +
            ", movingDirection=" + movingDirection +
            ", actions=" + actions +
            '}';
    }
}
