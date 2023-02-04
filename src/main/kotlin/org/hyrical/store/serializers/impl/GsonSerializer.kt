package org.hyrical.store.serializers.impl

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.LongSerializationPolicy
import org.hyrical.store.serializers.Serializer
import java.lang.reflect.Type

class GsonSerializer : Serializer() {

    private val gson: Gson = GsonBuilder().setLongSerializationPolicy(LongSerializationPolicy.STRING).serializeNulls().create()

    override fun <T> deserialize(json: String?, type: Class<T>): T? {
        if (json == null) return null

        return gson.fromJson(json, type)
    }

    override fun <T> deserialize(json: String?, type: Type): T? {
        if (json == null) return null

        return gson.fromJson(json, type)
    }

    override fun <T> serialize(obj: T): String? {
        return gson.toJson(obj)
    }

}