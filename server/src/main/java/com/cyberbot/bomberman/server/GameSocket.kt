package com.cyberbot.bomberman.server

import java.io.IOException
import java.net.DatagramPacket

interface GameSocket {
    @Throws(IOException::class)
    fun send(packet: DatagramPacket)
}
