package com.cyberbot.bomberman.server

import com.cyberbot.bomberman.core.models.net.packets.ClientRegisterRequest
import com.cyberbot.bomberman.core.models.net.packets.ControlPacket
import com.cyberbot.bomberman.core.models.net.packets.registerControlPacketAdapter
import com.cyberbot.bomberman.core.utils.fromJsonOrNull
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException
import com.google.gson.stream.JsonReader
import java.io.IOException
import java.net.Socket

class ClientControlService(private val clientSocket: Socket, private val controller: ClientController) : Runnable {
    @Volatile
    var running = true

    override fun run() {
        try {
            val input = clientSocket.getInputStream().reader()
            val output = clientSocket.getOutputStream().writer()
            val gson = GsonBuilder().registerControlPacketAdapter().create()
            val jsonReader = JsonReader(input)

            while (running) {
                val response = handleJson(gson, jsonReader)
                response?.let {
                    // For Gson to use the proper adapter type has to be explicitly set to the superclass
                    val json = gson.toJson(it, ControlPacket::class.java)
                    output.write(json)
                    output.flush()
                }
            }
        } catch (e: IOException) {
            running = false
        } finally {
            clientSocket.close()
        }
    }

    private fun handleJson(gson: Gson, reader: JsonReader): ControlPacket? {
        return try {
            when (val packet: ControlPacket? = gson.fromJsonOrNull(reader)) {
                is ClientRegisterRequest -> {
                    controller.onClientRegister(packet, this)
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