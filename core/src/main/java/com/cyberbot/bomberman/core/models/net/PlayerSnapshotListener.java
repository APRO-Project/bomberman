package com.cyberbot.bomberman.core.models.net;

import com.cyberbot.bomberman.core.models.net.packets.PlayerSnapshotPacket;

public interface PlayerSnapshotListener {
    void onNewSnapshot(PlayerSnapshotPacket snapshot);
}
