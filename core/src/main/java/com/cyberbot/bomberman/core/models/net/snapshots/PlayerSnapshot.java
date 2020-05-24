package com.cyberbot.bomberman.core.models.net.snapshots;

import com.cyberbot.bomberman.core.models.actions.Action;

import java.util.ArrayList;
import java.util.List;

public final class PlayerSnapshot {
    public final List<List<Action>> actions;

    public PlayerSnapshot() {
        actions = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "PlayerSnapshot{" +
            ", actions=" + actions +
            '}';
    }
}
