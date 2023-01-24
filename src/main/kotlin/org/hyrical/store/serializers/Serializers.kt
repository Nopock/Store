package org.hyrical.store.serializers

import org.hyrical.store.serializers.impl.GsonSerializer

object Serializers {
    var activeSerializer: Serializer = GsonSerializer()

    fun bind(serializer: Serializer) {
        activeSerializer = serializer
    }
}