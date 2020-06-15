package com.cyberbot.bomberman.core.utils

import com.github.salomonbrys.kotson.typeToken
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import java.io.Reader
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

inline fun <reified T : Any> Gson.fromJsonOrNull(json: Reader): T? = fromJson(json, typeToken<T>())

inline fun <reified T : Any> Gson.fromJsonOrNull(json: String): T? = fromJson(json, typeToken<T>())

inline fun <reified T : Any> Gson.fromJsonOrNull(json: JsonReader): T? = fromJson(json, typeToken<T>())

fun <V, T : ScheduledExecutorService> T.schedule(
    delay: Long,
    unit: TimeUnit = TimeUnit.MILLISECONDS,
    action: () -> V
): ScheduledFuture<V> {
    return this.schedule(action, delay, unit)
}

fun <T : ScheduledExecutorService> T.scheduleAtFixedRate(
    period: Long,
    delay: Long = 0,
    unit: TimeUnit = TimeUnit.MILLISECONDS,
    action: () -> Unit
): ScheduledFuture<*> {
    return this.scheduleAtFixedRate({ action() }, delay, period, unit)
}
