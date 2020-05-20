package com.cyberbot.bomberman.core.controllers;

import com.cyberbot.bomberman.core.models.actions.Action;
import com.cyberbot.bomberman.core.models.snapshots.PlayerSnapshot;
import com.cyberbot.bomberman.core.utils.Utils;
import org.apache.commons.collections4.queue.CircularFifoQueue;

import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;

public class SnapshotQueue implements MovementListener, ActionListener, Iterable<PlayerSnapshot> {
    private final CircularFifoQueue<PlayerSnapshot> queue;
    private PlayerSnapshot currentSnapshot;
    private int latestSequence;

    public SnapshotQueue(int bufferSize) {
        queue = new CircularFifoQueue<>(bufferSize);

        int sequence = ThreadLocalRandom.current().nextInt();
        currentSnapshot = new PlayerSnapshot(0);
    }

    @Override
    public void executeAction(Action action) {
        currentSnapshot.actions.add(action);
    }

    @Override
    public void move(int direction) {
        currentSnapshot.movingDirection = direction;
    }

    public int removeUntil(int latestSequence) {
        int removed = 0;
        while (queue.size() > 0 && Utils.isSequenceNext(latestSequence, queue.peek().sequence)) {
            removed++;
            queue.remove();
        }
        return removed;
    }

    public int size() {
        return queue.size();
    }

    public Integer getOldestSequence() {
        return queue.peek() != null ? queue.peek().sequence : null;
    }

    public Integer getLatestSequence() {
        return latestSequence;
    }

    public boolean isFull() {
        return queue.isFull();
    }

    public PlayerSnapshot createSnapshot() {
        PlayerSnapshot previousSnapshot = currentSnapshot;
        queue.add(previousSnapshot);
        currentSnapshot = new PlayerSnapshot(previousSnapshot.sequence + 1);
        latestSequence = previousSnapshot.sequence;
        return previousSnapshot;
    }

    @Override
    public Iterator<PlayerSnapshot> iterator() {
        return queue.iterator();
    }
}
