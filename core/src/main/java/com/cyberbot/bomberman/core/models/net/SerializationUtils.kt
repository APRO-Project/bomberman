package com.cyberbot.bomberman.core.models.net

import com.cyberbot.bomberman.core.models.Serializable
import com.cyberbot.bomberman.core.models.net.packets.GameSnapshotPacket
import com.cyberbot.bomberman.core.models.net.packets.PlayerSnapshotPacket

/**
 * Fed up with Java, Kotlin from now on
 */
object SerializationUtils {
    fun deserialize(buf: ByteArray, length: Int, offset: Int = 0): Any? {
        return when (PayloadType.getByValue(buf[offset])) {
            PayloadType.PLAYER_SNAPSHOT -> PlayerSnapshotPacket.fromByteArray(buf, length - 1, offset + 1)
            PayloadType.GAME_SNAPSHOT -> GameSnapshotPacket.fromByteArray(buf, length - 1, offset + 1)
            null -> null
        }
    }

    fun serialize(o: Serializable): ByteArray {
        val type = ByteArray(1) { getPayloadType(o).value }
        return type + o.toByteArray()
    }

    private fun getPayloadType(o: Serializable): PayloadType {
        return when (o) {
            is PlayerSnapshotPacket -> PayloadType.PLAYER_SNAPSHOT
            is GameSnapshotPacket -> PayloadType.GAME_SNAPSHOT
            else -> throw IllegalArgumentException(
                "Object of type ${o.javaClass.simpleName} does not have a matching PayloadType"
            )
        }
    }
}