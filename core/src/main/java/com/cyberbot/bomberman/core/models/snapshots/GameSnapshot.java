package com.cyberbot.bomberman.core.models.snapshots;

import com.cyberbot.bomberman.core.models.net.EntityData;

import java.io.Serializable;
import java.util.List;

// TODO: Add tiles
public class GameSnapshot implements Serializable {
    public final int sequence;
    public final List<EntityData<?>> entities;

    public GameSnapshot(int sequence, List<EntityData<?>> entities) {
        this.sequence = sequence;
        this.entities = entities;
    }
}
