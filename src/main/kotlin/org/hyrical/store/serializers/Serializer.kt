package org.hyrical.store.serializers

import java.lang.reflect.Type

abstract class Serializer {

    abstract fun <T> deserialize(json: String?, type: Class<T>): T?

    abstract fun <T> serialize(obj: T): String?

    abstract fun <T> deserialize(json: String?, type: Type):  T?
}