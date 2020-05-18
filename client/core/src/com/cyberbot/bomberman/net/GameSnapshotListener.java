package com.cyberbot.bomberman.net;

import com.cyberbot.bomberman.core.models.snapshots.GameSnapshot;

public interface GameSnapshotListener {
    void onNewSnapshot(GameSnapshot snapshot);
}
