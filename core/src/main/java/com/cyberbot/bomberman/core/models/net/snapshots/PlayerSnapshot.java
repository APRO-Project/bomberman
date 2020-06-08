package com.cyberbot.bomberman.core.models.net.snapshots;

import com.cyberbot.bomberman.core.models.actions.Action;

import java.util.ArrayList;
import java.util.List;

// TODO: Remove java.io.Serializable when own serialization is completed
public final class PlayerSnapshot implements java.io.Serializable {
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
