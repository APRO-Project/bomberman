package com.cyberbot.bomberman.core.models.net.packets;

import com.cyberbot.bomberman.core.models.Serializable;
import com.cyberbot.bomberman.core.models.net.snapshots.PlayerSnapshot;
import com.cyberbot.bomberman.core.utils.Utils;

// TODO: Remove java.io.Serializable when own serialization is completed
public class PlayerSnapshotPacket implements Serializable, java.io.Serializable {
    private final int sequence;
    private final long clientId;
    private final PlayerSnapshot snapshot;


    public PlayerSnapshotPacket(int sequence, long clientId, PlayerSnapshot snapshot) {
        this.sequence = sequence;
        this.clientId = clientId;
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
        return Utils.toByteArray(this);
    }


    public static PlayerSnapshotPacket fromByteArray(byte[] buf, int length, int offset) {
        // TODO: Better serialization
        return (PlayerSnapshotPacket) Utils.fromByteArray(buf, offset, length);
    }

    public long getClientId() {
        return clientId;
    }
}