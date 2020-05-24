package com.cyberbot.bomberman.core.controllers;

import com.cyberbot.bomberman.core.models.actions.Action;
import com.cyberbot.bomberman.core.models.net.packets.PlayerSnapshotPacket;
import com.cyberbot.bomberman.core.models.net.snapshots.PlayerSnapshot;
import com.cyberbot.bomberman.core.utils.Utils;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class SnapshotQueue implements ActionListener, Iterable<PlayerSnapshotPacket> {
    private final CircularFifoQueue<PlayerSnapshotPacket> queue;
    private PlayerSnapshot currentSnapshot;
    private int latestSequence;

    public SnapshotQueue(int bufferSize) {
        queue = new CircularFifoQueue<>(bufferSize);

        latestSequence = 0;
        currentSnapshot = new PlayerSnapshot();
    }

    @Override
    public void onActions(List<Action> actions) {
        currentSnapshot.actions.add(actions);
    }

    public int removeUntil(int latestSequence) {
        int removed = 0;
        while (queue.size() > 0 && Utils.isSequenceNext(latestSequence, queue.peek().getSequence())) {
            removed++;
            queue.remove();
        }
        return removed;
    }

    public int size() {
        return queue.size();
    }

    public Integer getOldestSequence() {
        return queue.peek() != null ? queue.peek().getSequence() : null;
    }

    public Integer getLatestSequence() {
        return latestSequence;
    }

    public boolean isFull() {
        return queue.isFull();
    }

    public PlayerSnapshotPacket createSnapshot() {
        PlayerSnapshotPacket packet = new PlayerSnapshotPacket(latestSequence + 1, currentSnapshot);
        queue.add(packet);
        currentSnapshot = new PlayerSnapshot();
        latestSequence++;
        return packet;
    }

    public Stream<List<Action>> userInputStream() {
        return queue.stream().map(it -> it.getSnapshot().actions).flatMap(Collection::stream);
    }

    @NotNull
    @Override
    public Iterator<PlayerSnapshotPacket> iterator() {
        return queue.iterator();
    }
}
