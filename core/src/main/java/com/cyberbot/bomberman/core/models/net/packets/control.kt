package com.cyberbot.bomberman.core.models.net.packets

import com.google.gson.GsonBuilder
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory

private const val TYPE = "type"

open class ControlPacket

data class Client(val id: Long? = null, val nick: String? = null)

data class ClientRegisterRequest(val nick: String? = null) : ControlPacket()
data class ClientRegisterResponse(val success: Boolean? = null, val client: Client? = null) : ControlPacket()
data class ErrorResponse(val error: String? = null) : ControlPacket()

fun GsonBuilder.registerControlPacketAdapter(): GsonBuilder {
    val adapter = RuntimeTypeAdapterFactory.of(ControlPacket::class.java, TYPE)
    adapter.registerSubtype(ClientRegisterRequest::class.java, "register_request")
    adapter.registerSubtype(ClientRegisterResponse::class.java, "register_response")
    adapter.registerSubtype(ErrorResponse::class.java, "error")

    return registerTypeAdapterFactory(adapter)
}

