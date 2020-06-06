package com.cyberbot.bomberman.server

import com.cyberbot.bomberman.core.controllers.PlayerActionController
import com.cyberbot.bomberman.core.models.Updatable
import com.cyberbot.bomberman.core.models.actions.Action
import com.cyberbot.bomberman.core.models.entities.PlayerEntity
import com.cyberbot.bomberman.core.models.net.PlayerSnapshotListener
import com.cyberbot.bomberman.core.models.net.packets.PlayerSnapshotPacket
import org.apache.commons.collections4.queue.CircularFifoQueue

class PlayerSession constructor(playerEntity: PlayerEntity, queueSize: Int = 32) :
    PlayerSnapshotListener, Updatable {
    private val actionQueue: CircularFifoQueue<List<Action>> = CircularFifoQueue(queueSize)
    private val actionController: PlayerActionController = PlayerActionController(playerEntity)
    var sequence: Int = -1
        private set

    private var errors = 0

    fun pollActions(): List<Action> {
        return actionQueue.poll()
    }

    fun onError() {
        errors++
    }

    fun clearErrors() {
        errors = 0
    }

    fun addListener(listener: PlayerActionController.Listener) {
        actionController.addListener(listener)
    }

    override fun update(delta: Float) {
        val actions = actionQueue.poll()
        if (actions != null) {
            actionController.onActions(actions)
        }
        actionController.update(delta)
    }

    override fun onNewSnapshot(snapshot: PlayerSnapshotPacket) {
        actionQueue.addAll(snapshot.snapshot.actions)
        sequence = snapshot.sequence
    }
}