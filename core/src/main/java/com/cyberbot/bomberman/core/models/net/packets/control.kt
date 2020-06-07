package com.cyberbot.bomberman.core.models.net.packets

import com.cyberbot.bomberman.core.models.net.data.PlayerData
import com.google.gson.GsonBuilder
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory

private const val TYPE = "type"

open class ControlPacket

class Client(val id: Long? = null, val nick: String? = null, val password: String? = null)
class Lobby(val id: String? = null, var ownerId: Long? = null, clients: List<Client> = ArrayList()) {
    val clients = ArrayList(clients)

    companion object {
        fun stripPasswords(lobby: Lobby): Lobby {
            val clients = lobby.clients.map { Client(it.id, it.nick) }
            return Lobby(lobby.id, lobby.ownerId, clients)
        }
    }
}

class ClientRegisterRequest(val nick: String? = null, val password: String? = null) : ControlPacket()
class ClientRegisterResponse(val success: Boolean? = null, val client: Client? = null) : ControlPacket()

class LobbyCreateRequest : ControlPacket()
class LobbyCreateResponse(val success: Boolean? = null, val id: String? = null) : ControlPacket()

class LobbyJoinRequest(val id: String? = null) : ControlPacket()
class LobbyJoinResponse(val success: Boolean? = null) : ControlPacket()

class LobbyLeaveRequest : ControlPacket()

class GameStartRequest : ControlPacket()

class LobbyUpdate(val lobby: Lobby = Lobby(), val isOwner: Boolean? = null) : ControlPacket()

class GameStart(val port: Int, val playerInit: PlayerData) : ControlPacket()

class ErrorResponse(val error: String? = null) : ControlPacket()

fun GsonBuilder.registerControlPacketAdapter(): GsonBuilder {
    val adapter = RuntimeTypeAdapterFactory.of(ControlPacket::class.java, TYPE)
    adapter.registerSubtype(ClientRegisterRequest::class.java, "register_request")
    adapter.registerSubtype(ClientRegisterResponse::class.java, "register_response")

    adapter.registerSubtype(LobbyCreateRequest::class.java, "lobby_create_request")
    adapter.registerSubtype(LobbyCreateResponse::class.java, "lobby_create_response")

    adapter.registerSubtype(LobbyJoinRequest::class.java, "lobby_join_request")
    adapter.registerSubtype(LobbyJoinResponse::class.java, "lobby_join_response")

    adapter.registerSubtype(LobbyLeaveRequest::class.java, "lobby_leave")

    adapter.registerSubtype(GameStartRequest::class.java, "game_start_request")

    adapter.registerSubtype(LobbyUpdate::class.java, "lobby_update")
    adapter.registerSubtype(GameStart::class.java, "game_start")

    adapter.registerSubtype(ErrorResponse::class.java, "error")

    return registerTypeAdapterFactory(adapter)
}

