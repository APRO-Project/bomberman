package com.cyberbot.bomberman.core.models.net;

import com.cyberbot.bomberman.core.models.net.snapshots.GameSnapshot;
import com.cyberbot.bomberman.core.utils.Utils;

public class GameSnapshotPacket {
    private final int sequence;
    private final GameSnapshot snapshot;


    public GameSnapshotPacket(int sequence, GameSnapshot snapshot) {
        this.sequence = sequence;
        this.snapshot = snapshot;
    }

    public int getSequence() {
        return sequence;
    }

    public GameSnapshot getSnapshot() {
        return snapshot;
    }

    public byte[] toByteArray() {
        // TODO: Better serialization
        return Utils.toByteArray(this);
    }
}
