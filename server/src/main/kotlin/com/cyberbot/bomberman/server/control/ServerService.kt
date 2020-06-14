package com.cyberbot.bomberman.server.control

import com.cyberbot.bomberman.core.models.net.data.PlayerData
import com.cyberbot.bomberman.core.models.net.packets.*
import com.cyberbot.bomberman.core.utils.Utils
import com.cyberbot.bomberman.server.session.Session
import com.cyberbot.bomberman.server.session.SessionService
import com.cyberbot.bomberman.server.session.SessionStateListener
import org.apache.logging.log4j.kotlin.Logging
import java.io.IOException
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.util.concurrent.ThreadLocalRandom


class ServerService(
    private val port: Int,
    private val maxLobbyCount: Int = 5,
    private val lobbyIdLength: Int = 5,
    private val maxPlayersPerLobby: Int = 4,
    private val maxPlayerNickLength: Int = 20
) : ClientController, Runnable, Logging, SessionStateListener {
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
            logger.info { "Server started, bound to ${serverSocket.localPort}" }

            while (running) {
                val client = serverSocket.accept()
                Thread(ClientControlService(client, this), "ClientControlService ${clientHandlers.size}").start()
            }
        } catch (e: IOException) {
            logger.info("Server stopped")
            e.printStackTrace()
            running = false
        }
    }

    override fun onPacket(payload: ControlPacket, service: ClientControlService) {
        when (payload) {
            is ClientRegisterRequest -> onClientRegister(payload, service)
            is LobbyCreateRequest -> onLobbyCreate(service)
            is LobbyJoinRequest -> onLobbyJoin(payload, service)
            is LobbyLeaveRequest -> onLobbyLeave(service)
            is GameStartRequest -> onGameStart(service)
            else -> logger.error { "Unsupported packet type ${payload.javaClass.simpleName}" }
        }
    }

    override fun onClientDisconnected(service: ClientControlService) {
        logger.info { "Client disconnected: ${service.client?.id}" }
        clientHandlers.remove(service.client)
        removeClientFromLobby(service.client)
    }

    override fun onSessionStarted(session: SessionService) {
        // TODO: Send control packets to all clients present in the session
        logger.info { "Started game on session ${session.port}" }
    }

    override fun onSessionFinished(session: SessionService, leaderboard: LinkedHashSet<Long>) {
        logger.info { "Finished game on session ${session.port}" }

        sendLeaderboard(leaderboard)
        sessions.values.remove(session)
    }

    private fun onClientRegister(request: ClientRegisterRequest, service: ClientControlService) {
        service.apply {
            logger.debug { "Client register request: ${request.nick}" }
            val nick = request.nick
            if (nick == null || nick.isBlank() || nick.length > maxPlayerNickLength) {
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
                logger.info { "Client register failed" }
                return
            }

            client = newClient
            clientHandlers[newClient] = this
            registeredClients.add(newClient)

            logger.info { "Client logged in: ${newClient.id}" }
            sendPacket(ClientRegisterResponse(true, newClient))
        }
    }

    private fun onLobbyCreate(service: ClientControlService) {
        service.apply {
            if (lobbies.size < maxLobbyCount) {
                val lobby = createLobby(client!!)
                val id = lobby.id ?: throw RuntimeException("Created lobby missing id")
                lobbies[id] = lobby

                logger.info { "Created new lobby: $id" }

                sendPacket(LobbyCreateResponse(true, id))
            } else {
                logger.warn("Lobby limit reached, unable to create new lobby")
                sendPacket(LobbyCreateResponse(false))
            }
        }
    }

    private fun onLobbyJoin(request: LobbyJoinRequest, service: ClientControlService) {
        service.apply {
            val lobby = lobbies[request.id]
            if (lobby == null || lobby.locked) {
                sendPacket(LobbyJoinResponse(false))
                return
            }

            if (lobby.clients.size < maxPlayersPerLobby) {
                removeClientFromLobby(client)
                lobby.clients.add(client!!)

                sendPacket(LobbyJoinResponse(true))
                notifyLobbyChange(lobby)

                logger.debug { "Client ${client!!.id} joined lobby: ${lobby.id}" }
            } else {
                sendPacket(LobbyJoinResponse(false))
            }
        }
    }

    private fun onLobbyLeave(service: ClientControlService) {
        removeClientFromLobby(service.client)
    }

    private fun onGameStart(service: ClientControlService) {
        val lobby = lobbies.values.firstOrNull { it.ownerId == service.client!!.id } ?: return
        val lobbyId = lobby.id ?: throw RuntimeException("Lobby in lobbies without id")

        if (lobby.clients.size < 2) {
            service.sendPacket(ErrorResponse("At least 2 players are required to start the game"))
            return
        }

        val session = SessionService()
        session.listeners.add(this)
        sessions[lobbyId] = session

        logger.info { "Staring game on port ${session.port} with ${lobby.clients.size} clients" }

        // Bartek genius - id's are random, so sorting by id yields random spawn
        lobby.clients.sortedBy { it.id }.forEachIndexed { i, c ->
            val id = c.id ?: throw RuntimeException("Client without id")
            val data = PlayerData(id, Session.getPlayerSpawnPosition(i), i)

            session.addClient(c.id!!, data)
            // Clients has to contain a client that's present in a lobby
            clientHandlers[c]!!.sendPacket(GameStart(session.port, data, lobby))
        }
        lobby.locked = true

        Thread(session, "Session Thread - ${session.port}").start()
    }

    private fun removeClientFromLobby(client: Client?) {
        val removeLobbies = ArrayList<String>()
        lobbies.map { it.value }.forEach {
            if (it.clients.remove(client)) {
                logger.debug { "Client ${client?.id} left lobby: ${it.id}" }
                if (it.clients.size == 0) {
                    removeLobbies.add(it.id!!)
                } else {
                    if (it.clients.size == 1) {
                        it.ownerId = it.clients[0].id
                    }
                    notifyLobbyChange(it)
                }
            }
        }

        removeLobbies.forEach {
            lobbies.remove(it)
        }
    }

    private fun notifyLobbyChange(lobby: Lobby) {
        val strippedLobby = Lobby.stripPasswords(lobby)
        val lobbyUpdate = LobbyUpdate(strippedLobby, false)

        lobby.clients
            .filter { it.id != lobby.ownerId }
            .map { clientHandlers[it] }
            .forEach { it?.sendPacket(lobbyUpdate) }

        val owner = lobby.clients.firstOrNull { it.id == lobby.ownerId }

        clientHandlers[owner]?.sendPacket(LobbyUpdate(strippedLobby, true))
    }

    private fun sendLeaderboard(leaderboardSet: LinkedHashSet<Long>) {
        val clients = leaderboardSet.mapNotNull { clientHandlers.keys.firstOrNull { client -> it == client.id } }
        val leaderboard = LinkedHashSet(clients)

        leaderboard.map { clientHandlers[it] }.forEach { it?.sendPacket(GameEnd(leaderboard)) }
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