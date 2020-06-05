package com.cyberbot.bomberman.server

import com.badlogic.gdx.backends.lwjgl.LwjglApplication

internal object Server {
    @JvmStatic
    fun main(args: Array<String>) {
        LwjglApplication(Application())
    }
}