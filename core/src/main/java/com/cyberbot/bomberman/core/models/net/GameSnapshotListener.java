package com.cyberbot.bomberman.core.models.net;

import com.cyberbot.bomberman.core.models.net.packets.GameSnapshotPacket;

public interface GameSnapshotListener {
    void onNewSnapshot(GameSnapshotPacket snapshot);
}
