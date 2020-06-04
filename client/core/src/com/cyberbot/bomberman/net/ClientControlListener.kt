package com.cyberbot.bomberman.net

import com.cyberbot.bomberman.core.models.net.packets.*

interface ClientControlListener {
    fun onClientConnected()

    fun onLobbyCreate(payload: LobbyCreateResponse)

    fun onLobbyJoin(payload: LobbyJoinResponse)

    fun onLobbyUpdate(payload: LobbyUpdate)

    fun onGameStart(payload: GameStart)

    fun onError(packet: ErrorResponse)
}