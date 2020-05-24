package com.cyberbot.bomberman.core.models.net.packets

import com.cyberbot.bomberman.core.models.Serializable
import com.cyberbot.bomberman.core.models.net.SerializationUtils
import com.cyberbot.bomberman.core.models.net.toBytes

data class Player(val nick: String, val textureVariant: Int)

data class LobbyCreateRequest(val name: String) : Serializable {
    companion object {
        fun fromByteArray(buf: ByteArray, offset: Int): LobbyCreateRequest {
            val name = SerializationUtils.deserializeString(buf, offset)

            return LobbyCreateRequest(name.first)
        }
    }

    override fun toByteArray(): ByteArray {
        return name.toBytes()
    }
}


data class LobbyCreateResponse(val name: String, val id: String) : Serializable {
    companion object {
        fun fromByteArray(buf: ByteArray, offset: Int): LobbyCreateResponse {
            @Suppress("NAME_SHADOWING")
            var offset = offset
            val name = SerializationUtils.deserializeString(buf, offset)
            offset += name.second

            val id = SerializationUtils.deserializeString(buf, offset)
            offset += id.second

            return LobbyCreateResponse(name.first, id.first)
        }
    }

    override fun toByteArray(): ByteArray {
        return name.toBytes() + id.toBytes()
    }

}

data class LobbyJoinRequest(val id: String)

data class LobbyJoinResponse(val id: String, val success: Boolean)

data class LobbyInfoPacket(val players: List<Player>)