package com.cyberbot.bomberman.server.control

import com.cyberbot.bomberman.core.models.net.packets.ClientRegisterRequest
import com.cyberbot.bomberman.core.models.net.packets.GameStartRequest
import com.cyberbot.bomberman.core.models.net.packets.LobbyCreateRequest
import com.cyberbot.bomberman.core.models.net.packets.LobbyJoinRequest

interface ClientController {
    fun onClientRegister(request: ClientRegisterRequest, service: ClientControlService)

    fun onLobbyCreate(request: LobbyCreateRequest, service: ClientControlService)

    fun onLobbyJoin(request: LobbyJoinRequest, service: ClientControlService)

    fun onLobbyLeave(service: ClientControlService)

    fun onGameStart(request: GameStartRequest, service: ClientControlService)

    fun onClientDisconnected(service: ClientControlService)
}
