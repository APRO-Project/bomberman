package com.cyberbot.bomberman.server.control

import com.cyberbot.bomberman.core.models.net.packets.ControlPacket

interface ClientController {
    fun onPacket(payload: ControlPacket, service: ClientControlService)

    fun onClientDisconnected(service: ClientControlService)
}
