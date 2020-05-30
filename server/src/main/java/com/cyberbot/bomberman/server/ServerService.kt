package com.cyberbot.bomberman.server

import com.cyberbot.bomberman.core.models.net.packets.Client
import com.cyberbot.bomberman.core.models.net.packets.ClientRegisterRequest
import com.cyberbot.bomberman.core.models.net.packets.ClientRegisterResponse
import java.io.IOException
import java.net.ServerSocket
import java.util.concurrent.ThreadLocalRandom

class ServerService(private val port: Int) : ClientController, Runnable {
    private val sessions = HashMap<Int, SessionService>()
    private val clients = HashMap<Client, ClientControlService>()


    @Volatile
    private var running = false
    private var serverSocket: ServerSocket? = null

    override fun run() {
        try {
            running = true
            serverSocket = ServerSocket(port)
            while (running) {
                val client = serverSocket!!.accept()
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