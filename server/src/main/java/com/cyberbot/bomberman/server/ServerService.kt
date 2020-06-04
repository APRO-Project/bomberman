package com.cyberbot.bomberman.server

import com.cyberbot.bomberman.core.models.items.Inventory
import com.cyberbot.bomberman.core.models.net.data.PlayerData
import com.cyberbot.bomberman.core.models.net.packets.*
import com.cyberbot.bomberman.core.utils.Utils
import java.io.IOException
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.concurrent.schedule


class ServerService(
    private val port: Int,
    private val maxLobbyCount: Int = 5,
    private val lobbyIdLength: Int = 5,
    private val maxPlayersPerLobby: Int = 4
) : ClientController, Runnable {
    private val sessions = HashMap<String, SessionService>()
    private val clients = HashMap<Client, ClientControlService>()
    private val lobbies = HashMap<String, Lobby>()

    @Volatile
    private var running = false
    private var serverSocket = ServerSocket()

    override fun run() {
        try {
            running = true
            serverSocket.bind(InetSocketAddress(port))
            while (running) {
                val client = serverSocket.accept()
                Thread(ClientControlService(client, this)).start()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            running = false
        }
    }

    override fun onClientRegister(
        request: ClientRegisterRequest,
        service: ClientControlService
    ): ClientRegisterResponse {
        request.nick ?: return ClientRegisterResponse(false)
        val client = createClient(request.nick!!)

        client?.also {
            clients[it] = service
        }

        return ClientRegisterResponse(client != null, client)
    }

    override fun onLobbyCreate(request: LobbyCreateRequest, client: Client): LobbyCreateResponse {
        return if (lobbies.size < maxLobbyCount) {
            val lobby = createLobby(client)
            val id = lobby.id ?: throw RuntimeException("Created lobby missing id")
            lobbies[id] = lobby

            LobbyCreateResponse(true, id)
        } else {
            LobbyCreateResponse(false)
        }
    }

    override fun onLobbyJoin(request: LobbyJoinRequest, client: Client): LobbyJoinResponse? {
        val lobby = lobbies[request.id] ?: return LobbyJoinResponse(false)

        return if (lobby.clients.size < maxPlayersPerLobby) {
            lobby.clients.add(client)

            // Quick and dirty way to first send the response to a client and only later the update packet
            Timer().schedule(100) {
                notifyLobbyChange(lobby)
            }

            LobbyJoinResponse(true)
        } else {
            LobbyJoinResponse(false)
        }
    }

    override fun onGameStart(request: GameStartRequest, client: Client) {
        val lobby = lobbies.values.firstOrNull { it.ownerId == client.id } ?: return
        val lobbyId = lobby.id ?: throw RuntimeException("Lobby in lobbies without id")

        val session = SessionService()
        sessions[lobbyId] = session

        lobby.clients.forEachIndexed { i, c ->
            val id = c.id ?: throw RuntimeException("Client without id")
            val data = PlayerData(id, Session.getPlayerSpawnPosition(i), Inventory(), i)

            session.addClient(c.id!!, data)
            // Clients has to contain a client that's present in a lobby
            clients[c]!!.sendPacket(GameStart(session.port, data))
        }

        Thread(session).start()
    }

    private fun notifyLobbyChange(lobby: Lobby) {
        val lobbyUpdate = LobbyUpdate(System.currentTimeMillis(), Lobby.stripIds(lobby))
        lobby.clients.map { clients[it] }.forEach { it?.sendPacket(lobbyUpdate) }
    }

    private fun createLobby(owner: Client): Lobby {
        // Remove any lobbies previously created by this client
        lobbies.entries.removeIf { it.value.ownerId == owner.id }

        var id: String

        do {
            id = Utils.generateLobbyId(lobbyIdLength)
        } while (lobbies.keys.contains(id))

        return Lobby(id, owner.id, ArrayList())
    }

    private fun createClient(nick: String): Client? {
        val clients = clients.map { it.key }
        if (clients.map { it.nick }.contains(nick)) {
            return null
        }

        val ids = clients.map { it.id }

        var id: Long
        do {
            id = ThreadLocalRandom.current().nextLong()
        } while (ids.contains(id))

        return Client(id, nick)
    }
}