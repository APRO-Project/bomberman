package com.cyberbot.bomberman.core.models.net.packets

import com.cyberbot.bomberman.core.models.net.data.PlayerData
import com.google.gson.GsonBuilder
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory

private const val TYPE = "type"

open class ControlPacket

class Client(val id: Long? = null, val nick: String? = null)
class Lobby(val id: String? = null, val ownerId: Long? = null, clients: List<Client> = ArrayList()) {
    val clients = ArrayList(clients)

    companion object {
        fun stripIds(lobby: Lobby): Lobby {
            val clients = lobby.clients.map { Client(nick = it.nick) }
            return Lobby(id = lobby.id, clients = clients)
        }
    }
}

class ClientRegisterRequest(val nick: String? = null) : ControlPacket()
class ClientRegisterResponse(val success: Boolean? = null, val client: Client? = null) : ControlPacket()

class LobbyCreateRequest : ControlPacket()
class LobbyCreateResponse(val success: Boolean? = null, val id: String? = null) : ControlPacket()

class LobbyJoinRequest(val id: String? = null) : ControlPacket()
class LobbyJoinResponse(val success: Boolean? = null) : ControlPacket()

class GameStartRequest : ControlPacket()

class LobbyUpdate(val timestamp: Long? = null, val lobby: Lobby = Lobby()) : ControlPacket()
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

    adapter.registerSubtype(GameStartRequest::class.java, "game_start_request")

    adapter.registerSubtype(LobbyUpdate::class.java, "lobby_update")
    adapter.registerSubtype(GameStart::class.java, "game_start")

    adapter.registerSubtype(ErrorResponse::class.java, "error")

    return registerTypeAdapterFactory(adapter)
}

