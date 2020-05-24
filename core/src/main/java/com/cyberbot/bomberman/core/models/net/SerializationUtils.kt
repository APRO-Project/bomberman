package com.cyberbot.bomberman.core.models.net

import com.cyberbot.bomberman.core.models.Serializable
import com.cyberbot.bomberman.core.models.net.packets.GameSnapshotPacket
import com.cyberbot.bomberman.core.models.net.packets.PlayerSnapshotPacket

/**
 * Fed up with Java, Kotlin from now on
 */
object SerializationUtils {
    @Throws(InvalidPacketFormatException::class)
    fun deserialize(buf: ByteArray, length: Int, offset: Int = 0): Any {
        return when (PayloadType.getByValue(buf[offset])) {
            PayloadType.PLAYER_SNAPSHOT -> PlayerSnapshotPacket.fromByteArray(buf, length, offset + 1)
            PayloadType.GAME_SNAPSHOT -> GameSnapshotPacket.fromByteArray(buf, length, offset + 1)
            PayloadType.LOBBY_CREATE_REQUEST -> TODO()
            PayloadType.LOBBY_CREATE_RESPONSE -> TODO()
            PayloadType.LOBBY_JOIN_REQUEST -> TODO()
            PayloadType.LOBBY_JOIN_RESPONSE -> TODO()
            null -> throw InvalidPacketFormatException("First byte of each packet should contain the payload type")
        }
    }

    fun serialize(o: Serializable): ByteArray {
        val type = ByteArray(1) { getPayloadType(o).value }
        return type + o.toByteArray()
    }

    private fun getPayloadType(o: Serializable): PayloadType {
        return when (o) {
            is PlayerSnapshotPacket -> PayloadType.PLAYER_SNAPSHOT
            is GameSnapshotPacket -> PayloadType.PLAYER_SNAPSHOT
            else -> throw IllegalArgumentException(
                "Object of type ${o.javaClass.simpleName} does not have a matching PayloadType"
            )
        }
    }
}