package com.cyberbot.bomberman.core.models.net;

public interface GameSnapshotListener {
    void onNewSnapshot(GameSnapshotPacket snapshot);
}
