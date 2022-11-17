package org.hyrical.store.serializers

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.LongSerializationPolicy
import org.hyrical.store.serializers.impl.GsonSerializer

object Serializers {
    var activeSerialize: Serializer = GsonSerializer()

    fun bind(serializer: Serializer) {
        activeSerialize = serializer
    }
}