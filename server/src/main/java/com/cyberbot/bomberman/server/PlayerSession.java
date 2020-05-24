package com.cyberbot.bomberman.server;

import com.cyberbot.bomberman.core.controllers.PlayerActionController;
import com.cyberbot.bomberman.core.models.Updatable;
import com.cyberbot.bomberman.core.models.actions.Action;
import com.cyberbot.bomberman.core.models.entities.PlayerEntity;
import com.cyberbot.bomberman.core.models.net.PlayerSnapshotListener;
import com.cyberbot.bomberman.core.models.net.packets.PlayerSnapshotPacket;
import org.apache.commons.collections4.queue.CircularFifoQueue;

import java.util.List;

public class PlayerSession implements PlayerSnapshotListener, Updatable {
    private final PlayerEntity playerEntity;
    private final CircularFifoQueue<List<Action>> actionQueue;
    private final PlayerActionController actionController;
    private int sequence;
    private int errors;

    public PlayerSession(PlayerEntity playerEntity) {
        this(playerEntity, 32);
    }

    public PlayerSession(PlayerEntity playerEntity, int queueSize) {
        this.playerEntity = playerEntity;
        actionQueue = new CircularFifoQueue<>(queueSize);
        actionController = new PlayerActionController(playerEntity);
        sequence = -1;
    }

    public List<Action> pollActions() {
        return actionQueue.poll();
    }

    public void onError() {
        errors++;
    }

    public void clearErrors() {
        errors = 0;
    }

    @Override
    public void update(float delta) {
        List<Action> actions = actionQueue.poll();
        if (actions != null) {
            actionController.onActions(actions);
        }
    }

    @Override
    public void onNewSnapshot(PlayerSnapshotPacket snapshot) {
        actionQueue.addAll(snapshot.getSnapshot().actions);
        sequence = snapshot.getSequence();
    }

    public int getSequence() {
        return sequence;
    }
}
