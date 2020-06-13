package com.cyberbot.bomberman.server.control

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
import java.net.SocketException

class ClientControlService(private val clientSocket: Socket, private val controller: ClientController) : Runnable {

    @Volatile
    private var running = false

    var client: Client? = null

    private lateinit var gson: Gson
    private lateinit var reader: JsonReader
    private lateinit var writer: JsonWriter

    override fun run() {
        try {
            clientSocket.keepAlive = true
            writer = JsonWriter(clientSocket.getOutputStream().writer().buffered())
            reader = JsonReader(clientSocket.getInputStream().reader().buffered())
            gson = GsonBuilder().registerControlPacketAdapter().create()

            running = true

            while (running) {
                handleJson()
            }
        } catch (ignored: SocketException) {
            // The socket closed, just ignore
        } finally {
            running = false

            try {
                reader.close()
                writer.close()
                clientSocket.close()
            } catch (ignored: IOException) {
                // Ignore socket close exception
            }

            controller.onClientDisconnected(this)
        }
    }

    fun sendPacket(packet: ControlPacket) {
        check(running) { "Attempting to send a packet when the service is not running" }

        // For Gson to use the proper adapter type has to be explicitly set to the superclass
        gson.toJson(packet, ControlPacket::class.java, writer)
        writer.flush()
    }

    private fun handleJson() {
        try {
            val packet: ControlPacket? = gson.fromJsonOrNull(reader)

            if (client == null && packet !is ClientRegisterRequest) {
                sendPacket(ErrorResponse("First packet should be a register packet"))
            }

            packet?.let { controller.onPacket(it, this) }
        } catch (e: JsonSyntaxException) {
            // This cursed shenanigans are required,
            // because Gson catches the IOException and rethrows as JsonSyntaxException
            // and we still need to ignore the syntax exceptions.
            // From Gson: "Figure out whether it is indeed right to rethrow this as JsonSyntaxException"
            if (e.cause is SocketException) {
                throw e.cause as SocketException
            }
        } catch (ignored: JsonParseException) {

        }
    }
}