package com.cyberbot.bomberman.core.models.net;

import com.cyberbot.bomberman.core.models.net.snapshots.PlayerSnapshot;

public interface PlayerSnapshotListener {
    void onNewSnapshot(PlayerSnapshot snapshot);
}
