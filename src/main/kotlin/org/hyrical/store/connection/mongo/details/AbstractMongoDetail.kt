package org.hyrical.store.connection.mongo.details

abstract class AbstractMongoDetail(val database: String) {
    abstract fun getURI(): String
}