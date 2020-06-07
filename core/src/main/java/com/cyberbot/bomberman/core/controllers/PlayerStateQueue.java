package com.cyberbot.bomberman.core.controllers;

import com.cyberbot.bomberman.core.models.PlayerState;
import org.apache.commons.collections4.queue.CircularFifoQueue;

public class PlayerStateQueue {
    private final CircularFifoQueue<PlayerState> queue;

    public PlayerStateQueue(int bufferSize) {
        this.queue = new CircularFifoQueue<>(bufferSize);
    }

    public PlayerState removeUntil(int latestSequence) {
        if (latestSequence == -1) {
            return null;
        }

        PlayerState removed = null;
        while (queue.size() > 0 && latestSequence >= queue.peek().sequence) {
            removed = queue.remove();
        }

        if (removed != null && removed.sequence == latestSequence) {
            return removed;
        }

        return null;
    }

    public int size() {
        return queue.size();
    }

    public void clear() {
        queue.clear();
    }

    public void addState(PlayerState state) {
        queue.add(state);
    }
}
