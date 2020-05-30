package com.cyberbot.bomberman.server

import com.cyberbot.bomberman.core.models.net.InvalidPacketFormatException
import com.cyberbot.bomberman.core.models.net.SerializationUtils.deserialize
import com.cyberbot.bomberman.core.models.net.packets.PlayerSnapshotPacket
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket

class SessionService(port: Int? = null, private val bufferSize: Int = 4096) : GameSocket, Runnable {
    val port: Int
    private val socket: DatagramSocket

    init {
        if (port == null) {
            socket = DatagramSocket()
            this.port = socket.port
        } else {
            socket = DatagramSocket(port)
            this.port = port
        }
    }

    @Volatile
    private var running = false
    private val session: Session = Session(this)

    override fun run() {
        try {
            running = true

            val buffer = ByteArray(bufferSize)
            val packet = DatagramPacket(buffer, buffer.size)
            while (running) {
                socket.receive(packet)
                val client = ClientConnection.fromDatagramPacket(packet)
                val snapshot = deserialize(buffer, packet.length, 0)
                        as? PlayerSnapshotPacket ?: continue
                session.onSnapshot(client, snapshot)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InvalidPacketFormatException) {
            e.printStackTrace()
        } finally {
            running = false
            socket.close()
        }
    }

    @Throws(IOException::class)
    override fun send(packet: DatagramPacket) {
        if (!running) {
            throw IllegalStateException("Attempting to send a packet when the service is not running")
        }

        socket.send(packet)
    }
}