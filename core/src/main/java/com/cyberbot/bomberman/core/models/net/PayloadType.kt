package com.cyberbot.bomberman.core.models.net

/**
 * Using Kotlin because Java sucks, and for some weird ass reason you cannot define byte literals.
 */
enum class PayloadType(val value: Byte) {
    PLAYER_SNAPSHOT(0),
    GAME_SNAPSHOT(1);

    companion object {
        private val values = values()
        fun getByValue(value: Byte) = values.firstOrNull { it.value == value }
    }
}