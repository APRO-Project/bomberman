package com.cyberbot.bomberman.core.models.net;

import com.cyberbot.bomberman.core.models.snapshots.GameSnapshot;

public interface GameSnapshotListener {
    void onNewSnapshot(GameSnapshot snapshot);
}
