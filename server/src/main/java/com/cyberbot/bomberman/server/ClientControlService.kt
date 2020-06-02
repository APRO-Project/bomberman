package com.cyberbot.bomberman.server

import com.cyberbot.bomberman.core.models.net.packets.*
import com.cyberbot.bomberman.core.utils.fromJsonOrNull
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException
import com.google.gson.stream.JsonReader
import java.io.IOException
import java.io.PrintWriter
import java.net.Socket

class ClientControlService(private val clientSocket: Socket, private val controller: ClientController) : Runnable {

    @Volatile
    private var running = true

    private var client: Client? = null
    private lateinit var gson: Gson
    private lateinit var output: PrintWriter

    override fun run() {
        try {
            val input = clientSocket.getInputStream().reader()
            output = PrintWriter(clientSocket.getOutputStream().writer())
            gson = GsonBuilder().registerControlPacketAdapter().create()
            val jsonReader = JsonReader(input)

            while (running) {
                val response = handleJson(gson, jsonReader)
                response?.let {
                    // For Gson to use the proper adapter type has to be explicitly set to the superclass
                    val json = gson.toJson(it, ControlPacket::class.java)
                    output.println(json)
                    output.flush()
                }
            }
        } catch (e: IOException) {
            running = false
        } finally {
            clientSocket.close()
        }
    }

    fun sendPacket(packet: ControlPacket) {
        check(running) { "Attempting to send a packet when the service is not running" }

        output.println(gson.toJson(packet, ControlPacket::class.java))
        output.flush()
    }

    private fun handleJson(gson: Gson, reader: JsonReader): ControlPacket? {
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