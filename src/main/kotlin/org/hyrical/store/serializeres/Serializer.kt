package org.hyrical.store.serializeres

abstract class Serializer {

    abstract fun <T> deserialize(json: String, type: Class<T>): T?

    abstract fun <T> serialize(obj: T): String?
}