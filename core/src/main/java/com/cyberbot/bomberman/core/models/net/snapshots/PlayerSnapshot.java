package com.cyberbot.bomberman.core.models.net.snapshots;

import com.cyberbot.bomberman.core.models.actions.Action;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public final class PlayerSnapshot implements Serializable {
    public final int sequence;

    public final List<List<Action>> actions;

    public PlayerSnapshot(int sequence) {
        this.sequence = sequence;
        actions = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "PlayerSnapshot{" +
            "sequence=" + sequence +
            ", actions=" + actions +
            '}';
    }
}
