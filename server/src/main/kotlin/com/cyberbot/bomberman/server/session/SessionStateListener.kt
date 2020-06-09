package com.cyberbot.bomberman.server.session

interface SessionStateListener {
    fun onSessionStarted(session: SessionService)

    fun onSessionFinished(session: SessionService)
}