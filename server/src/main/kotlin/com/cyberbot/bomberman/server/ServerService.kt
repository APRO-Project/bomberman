package com.cyberbot.bomberman.server

import com.cyberbot.bomberman.core.models.items.Inventory
import com.cyberbot.bomberman.core.models.net.data.PlayerData
import com.cyberbot.bomberman.core.models.net.packets.*
import com.cyberbot.bomberman.core.utils.Utils
import java.io.IOException
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.util.concurrent.ThreadLocalRandom


class ServerService(
    private val port: Int,
    private val maxLobbyCount: Int = 5,
    private val lobbyIdLength: Int = 5,
    private val maxPlayersPerLobby: Int = 4
) : ClientController, Runnable {
    private val sessions = HashMap<String, SessionService>()
    private val clientHandlers = HashMap<Client, ClientControlService>()
    private val registeredClients = ArrayList<Client>() // TODO: Load registered client from file
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

    override fun onClientRegister(request: ClientRegisterRequest, service: ClientControlService) {
        service.apply {
            val nick = request.nick
            if (nick == null) {
                sendPacket(ClientRegisterResponse(false))
                return
            }

            val password = request.password
            if (password == null) {
                sendPacket(ClientRegisterResponse(false))
                return
            }

            val newClient = loginClient(nick, password)
            if (newClient == null) {
                sendPacket(ClientRegisterResponse(false))
                return
            }

            client = newClient
            clientHandlers[newClient] = this
            registeredClients.add(newClient)

            sendPacket(ClientRegisterResponse(true, newClient))
        }
    }

    override fun onLobbyCreate(request: LobbyCreateRequest, service: ClientControlService) {
        service.apply {
            if (lobbies.size < maxLobbyCount) {
                val lobby = createLobby(client!!)
                val id = lobby.id ?: throw RuntimeException("Created lobby missing id")
                lobbies[id] = lobby

                sendPacket(LobbyCreateResponse(true, id))
            } else {
                sendPacket(LobbyCreateResponse(false))
            }
        }
    }

    override fun onLobbyJoin(request: LobbyJoinRequest, service: ClientControlService) {
        service.apply {
            val lobby = lobbies[request.id]
            if (lobby == null) {
                sendPacket(LobbyJoinResponse(false))
                return
            }

            if (lobby.clients.size < maxPlayersPerLobby) {
                lobby.clients.add(client!!)

                sendPacket(LobbyJoinResponse(true))
                notifyLobbyChange(lobby)
            } else {
                sendPacket(LobbyJoinResponse(false))
            }
        }
    }

    override fun onGameStart(request: GameStartRequest, service: ClientControlService) {
        val lobby = lobbies.values.firstOrNull { it.ownerId == service.client!!.id } ?: return
        val lobbyId = lobby.id ?: throw RuntimeException("Lobby in lobbies without id")

        val session = SessionService()
        sessions[lobbyId] = session

        lobby.clients.forEachIndexed { i, c ->
            val id = c.id ?: throw RuntimeException("Client without id")
            val data = PlayerData(id, Session.getPlayerSpawnPosition(i), Inventory(), i)

            session.addClient(c.id!!, data)
            // Clients has to contain a client that's present in a lobby
            clientHandlers[c]!!.sendPacket(GameStart(session.port, data))
        }

        Thread(session).start()
    }

    override fun onClientDisconnected(service: ClientControlService) {
        clientHandlers.remove(service.client)
    }

    private fun notifyLobbyChange(lobby: Lobby) {
        val strippedLobby = Lobby.stripIds(lobby)
        val lobbyUpdate = LobbyUpdate(strippedLobby, false)

        lobby.clients
            .filter { it.id != lobby.ownerId }
            .map { clientHandlers[it] }
            .forEach { it?.sendPacket(lobbyUpdate) }

        val owner = lobby.clients.first { it.id == lobby.ownerId }

        clientHandlers[owner]!!.sendPacket(LobbyUpdate(strippedLobby, true))
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

    private fun loginClient(nick: String, password: String): Client? {
        val client = registeredClients
            .firstOrNull { it.nick == nick }

        // Check if client has an active connection
        if (clientHandlers.containsKey(client)) {
            return null
        }

        if (client != null) {
            return if (client.password == password) client else null
        }

        val ids = registeredClients.map { it.id }

        var id: Long
        do {
            id = ThreadLocalRandom.current().nextLong()
        } while (ids.contains(id))

        return Client(id, nick, password)
    }
}