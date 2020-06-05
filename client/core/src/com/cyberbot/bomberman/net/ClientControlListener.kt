package com.cyberbot.bomberman.net

import com.cyberbot.bomberman.core.models.net.packets.*
import java.io.IOException

interface ClientControlListener {
    fun onClientConnected()

    fun onConnectionError(e: IOException)

    fun onClientDisconnected()

    fun onLobbyCreate(payload: LobbyCreateResponse)

    fun onLobbyJoin(payload: LobbyJoinResponse)

    fun onLobbyUpdate(payload: LobbyUpdate)

    fun onGameStart(payload: GameStart)

    fun onError(payload: ErrorResponse)

    fun onRegisterResponse(payload: ClientRegisterResponse)
}