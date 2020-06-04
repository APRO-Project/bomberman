package com.cyberbot.bomberman.server

import com.cyberbot.bomberman.core.models.net.packets.*

interface ClientController {
    fun onClientRegister(request: ClientRegisterRequest, service: ClientControlService): ClientRegisterResponse?

    fun onLobbyCreate(request: LobbyCreateRequest, client: Client): LobbyCreateResponse?

    fun onLobbyJoin(request: LobbyJoinRequest, client: Client): LobbyJoinResponse?

    fun onGameStart(request: GameStartRequest, client: Client)
}
