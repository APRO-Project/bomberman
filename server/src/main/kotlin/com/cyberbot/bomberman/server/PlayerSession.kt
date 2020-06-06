package com.cyberbot.bomberman.server

import com.cyberbot.bomberman.core.controllers.PlayerActionController
import com.cyberbot.bomberman.core.models.Updatable
import com.cyberbot.bomberman.core.models.actions.Action
import com.cyberbot.bomberman.core.models.entities.PlayerEntity
import com.cyberbot.bomberman.core.models.net.PlayerSnapshotListener
import com.cyberbot.bomberman.core.models.net.packets.PlayerSnapshotPacket
import org.apache.commons.collections4.queue.CircularFifoQueue
import java.util.*

class PlayerSession constructor(playerEntity: PlayerEntity, queueSize: Int = 32) :
    PlayerSnapshotListener, Updatable {
    private val packetQueue: Queue<Pair<Int, Queue<List<Action>>>> = CircularFifoQueue(queueSize)
    private val actionController: PlayerActionController = PlayerActionController(playerEntity)
    var sequence: Int = -1
        private set

    private var errors = 0

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
        if (packetQueue.isEmpty()) return

        var pair = packetQueue.peek() ?: return
        while (pair.second.isEmpty()) {
            sequence = pair.first
            pair = packetQueue.poll() ?: return
        }

        actionController.onActions(pair.second.poll())
        actionController.update(delta)
    }

    override fun onNewSnapshot(packet: PlayerSnapshotPacket) {
        packetQueue.add(Pair(packet.sequence, ArrayDeque(packet.snapshot.actions)))
    }
}