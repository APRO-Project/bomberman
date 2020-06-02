package com.cyberbot.bomberman.server

import java.net.DatagramPacket
import java.net.InetAddress

data class ClientConnection(val port: Int, val address: InetAddress) {
    companion object {
        fun fromDatagramPacket(packet: DatagramPacket): ClientConnection {
            return ClientConnection(packet.port, packet.address)
        }
    }
}