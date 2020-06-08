package com.cyberbot.bomberman.server

import com.cyberbot.bomberman.server.control.ServerService
import org.apache.logging.log4j.kotlin.Logging


internal object Server : Logging {
    @JvmStatic
    fun main(args: Array<String>) {
        val service = ServerService(8038)
        Thread(service, "ServerService Thread").start()
    }
}