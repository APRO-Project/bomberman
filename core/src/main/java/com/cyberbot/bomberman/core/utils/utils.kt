package com.cyberbot.bomberman.core.utils

import com.github.salomonbrys.kotson.typeToken
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import java.io.Reader

inline fun <reified T : Any> Gson.fromJsonOrNull(json: Reader): T? = fromJson(json, typeToken<T>())

inline fun <reified T : Any> Gson.fromJsonOrNull(json: String): T? = fromJson(json, typeToken<T>())

inline fun <reified T : Any> Gson.fromJsonOrNull(json: JsonReader): T? = fromJson(json, typeToken<T>())
