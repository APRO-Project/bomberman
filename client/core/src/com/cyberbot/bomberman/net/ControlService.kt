package com.cyberbot.bomberman.net

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
import java.net.SocketAddress
import java.net.SocketException

class ControlService(private val address: SocketAddress) : Runnable {
    val listeners = ArrayList<ClientControlListener>()

    private val socket = Socket()

    private lateinit var writer: JsonWriter
    private lateinit var gson: Gson
    private lateinit var reader: JsonReader

    @Volatile
    var running = false

    override fun run() {
        socket.keepAlive = true
        try {
            socket.connect(address)
        } catch (e: IOException) {
            listeners.forEach { it.onConnectionError(e) }
            return
        }

        try {
            writer = JsonWriter(socket.getOutputStream().writer().buffered())
            reader = JsonReader(socket.getInputStream().reader().buffered())

            gson = GsonBuilder().registerControlPacketAdapter().create()

            running = true

            listeners.forEach { it.onClientConnected() }

            while (running) {
                handleJson()
            }
        } catch (e: IOException) {
            listeners.forEach { it.onClientDisconnected() }
        } finally {
            writer.close()
            reader.close()
            socket.close()

            running = false
        }
    }

    fun sendPacket(packet: ControlPacket) {
        check(running) { "Attempting to send a packet when the service is not running" }

        gson.toJson(packet, ControlPacket::class.java, writer)
        writer.flush()
    }

    private fun handleJson() {
        try {
            when (val packet: ControlPacket? = gson.fromJsonOrNull(reader)) {
                is ClientRegisterResponse -> listeners.forEach { it.onRegisterResponse(packet) }
                is LobbyUpdate -> listeners.forEach { it.onLobbyUpdate(packet) }
                is LobbyCreateResponse -> listeners.forEach { it.onLobbyCreate(packet) }
                is LobbyJoinResponse -> listeners.forEach { it.onLobbyJoin(packet) }
                is GameStart -> listeners.forEach { it.onGameStart(packet) }
                is ErrorResponse -> listeners.forEach { it.onError(packet) }
            }
        } catch (e: JsonSyntaxException) {
            // This cursed shenanigans are required,
            // because Gson catches the IOException and rethrows as JsonSyntaxException
            // and we still need to ignore the syntax exceptions.
            // From Gson: "Figure out whether it is indeed right to rethrow this as JsonSyntaxException"
            if (e.cause is SocketException) {
                throw e.cause as SocketException
            }
        } catch (e: JsonParseException) {

        }
    }
}