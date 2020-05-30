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
            PayloadType.PLAYER_SNAPSHOT -> PlayerSnapshotPacket.fromByteArray(buf, length - 1, offset + 1)
            PayloadType.GAME_SNAPSHOT -> GameSnapshotPacket.fromByteArray(buf, length - 1, offset + 1)
            null -> throw InvalidPacketFormatException("First byte of each packet should contain the payload type")
        }
    }

    fun serialize(o: Serializable): ByteArray {
        val type = ByteArray(1) { getPayloadType(o).value }
        return type + o.toByteArray()
    }

    fun deserializeString(buf: ByteArray, offset: Int): Pair<String, Int> {
        val nameLength = buf[offset]
        val name = String(buf.copyOfRange(offset + 1, offset + 1 + nameLength))

        return Pair(name, nameLength + 1)
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

fun String.toBytes(): ByteArray {
    val str = this.toByteArray()
    val length = ByteArray(1) { str.size.toByte() }

    return length + str
}