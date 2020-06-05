package com.cyberbot.bomberman.server

import com.badlogic.gdx.ApplicationListener

class Application : ApplicationListener {
    override fun create() {
        val service = ServerService(8038)
        Thread(service).start()
    }

    override fun resize(width: Int, height: Int) {}
    override fun render() {}
    override fun pause() {}
    override fun resume() {}
    override fun dispose() {}
}