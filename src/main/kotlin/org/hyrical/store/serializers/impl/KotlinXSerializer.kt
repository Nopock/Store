package org.hyrical.store.serializers.impl

import kotlinx.serialization.*

class KotlinXSerializer : Serialize() {

    val serializer = Json { ignoreUnknownKeys = true}

    override fun <T> deserialize(json: String?, type: Class<T>): T? {
        if (json == null) return null

        return serializer.decodeFromString<T>(json!!)
    }

    override fun <T> serialize(obj: T): String? {
        return serializer.encodeToString(obj)
    }
}