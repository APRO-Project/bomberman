package com.cyberbot.bomberman.core.models.net.packets;

import com.cyberbot.bomberman.core.models.Serializable;
import com.cyberbot.bomberman.core.models.net.snapshots.GameSnapshot;
import com.cyberbot.bomberman.core.utils.Utils;

public class GameSnapshotPacket implements Serializable {
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

    @Override
    public byte[] toByteArray() {
        // TODO: Better serialization
        return Utils.toByteArray(this);
    }


    public static GameSnapshotPacket fromByteArray(byte[] buf, int length, int offset) {
        // TODO: Better serialization
        return (GameSnapshotPacket) Utils.fromByteArray(buf, offset, length);
    }
}
