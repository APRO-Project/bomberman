package com.cyberbot.bomberman.core.controllers;

import com.cyberbot.bomberman.core.models.actions.Action;
import com.cyberbot.bomberman.core.models.net.packets.PlayerSnapshotPacket;
import com.cyberbot.bomberman.core.models.net.snapshots.PlayerSnapshot;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class SnapshotQueue implements ActionListener, Iterable<PlayerSnapshotPacket> {
    private final CircularFifoQueue<PlayerSnapshotPacket> queue;
    private final long clientId;
    private PlayerSnapshot currentSnapshot;
    private int latestSequence;

    public SnapshotQueue(long clientId, int bufferSize) {
        this.queue = new CircularFifoQueue<>(bufferSize);
        this.latestSequence = 100;
        this.currentSnapshot = new PlayerSnapshot();
        this.clientId = clientId;
    }

    @Override
    public void onActions(List<Action> actions) {
        currentSnapshot.actions.add(actions);
    }

    public PlayerSnapshotPacket removeUntil(int latestSequence) {
        if (latestSequence == -1) {
            return null;
        }

        PlayerSnapshotPacket removed = null;
        while (queue.size() > 0 && latestSequence >= queue.peek().getSequence()) {
            removed = queue.remove();
        }

        return removed;
    }

    public int size() {
        return queue.size();
    }

    public int maxSize() {
        return queue.maxSize();
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
        PlayerSnapshotPacket packet = new PlayerSnapshotPacket(latestSequence + 1, clientId, currentSnapshot);
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
