package com.cyberbot.bomberman.core.models.net.packets;

import com.cyberbot.bomberman.core.models.Serializable;
import com.cyberbot.bomberman.core.models.net.snapshots.PlayerSnapshot;
import com.cyberbot.bomberman.core.utils.Utils;

public class PlayerSnapshotPacket implements Serializable {
    private final int sequence;
    private final PlayerSnapshot snapshot;


    public PlayerSnapshotPacket(int sequence, PlayerSnapshot snapshot) {
        this.sequence = sequence;
        this.snapshot = snapshot;
    }

    public int getSequence() {
        return sequence;
    }

    public PlayerSnapshot getSnapshot() {
        return snapshot;
    }

    @Override
    public byte[] toByteArray() {
        return new byte[0];
    }


    public static PlayerSnapshotPacket fromByteArray(byte[] buf, int length, int offset) {
        // TODO: Better serialization
        return (PlayerSnapshotPacket) Utils.fromByteArray(buf, offset, length);
    }
}