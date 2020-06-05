package com.cyberbot.bomberman.server

internal object Server {
    @JvmStatic
    fun main(args: Array<String>) {
        val service = ServerService(8038)
        Thread(service).start()
    }
}