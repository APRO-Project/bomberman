package com.cyberbot.bomberman.server

import com.cyberbot.bomberman.core.models.net.packets.*
import com.cyberbot.bomberman.core.utils.fromJsonOrNull
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.io.IOException
import java.net.Socket

class ClientControlService(private val clientSocket: Socket, private val controller: ClientController) : Runnable {

    @Volatile
    private var running = true

    private var client: Client? = null

    private lateinit var gson: Gson
    private lateinit var reader: JsonReader
    private lateinit var writer: JsonWriter

    override fun run() {
        try {
            clientSocket.keepAlive = true
            writer = JsonWriter(clientSocket.getOutputStream().writer().buffered())
            reader = JsonReader(clientSocket.getInputStream().reader().buffered())
            gson = GsonBuilder().registerControlPacketAdapter().create()

            while (running) {
                val response = handleJson()
                response?.let { sendPacket(it) }
            }
        } catch (e: IOException) {
            running = false
        } finally {
            clientSocket.close()
        }
    }

    fun sendPacket(packet: ControlPacket) {
        check(running) { "Attempting to send a packet when the service is not running" }

        // For Gson to use the proper adapter type has to be explicitly set to the superclass
        gson.toJson(packet, ControlPacket::class.java, writer)
        writer.flush()
    }

    private fun handleJson(): ControlPacket? {
        return try {
            val packet: ControlPacket? = gson.fromJsonOrNull(reader)

            if (client == null && packet !is ClientRegisterRequest) {
                ErrorResponse("First packet should be a register packet")
            }

            when (packet) {
                is ClientRegisterRequest -> {
                    val response = controller.onClientRegister(packet, this)
                    client = response?.client

                    response
                }
                is LobbyCreateRequest -> controller.onLobbyCreate(packet, client!!)
                is LobbyJoinRequest -> controller.onLobbyJoin(packet, client!!)
                is GameStartRequest -> {
                    controller.onGameStart(packet, client!!)
                    null
                }
                else -> null
            }
        } catch (e: JsonSyntaxException) {
            e.printStackTrace()
            null
        } catch (e: JsonParseException) {
            e.printStackTrace()
            null
        }
    }
}