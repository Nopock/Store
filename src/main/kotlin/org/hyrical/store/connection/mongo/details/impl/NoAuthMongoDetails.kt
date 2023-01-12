package org.hyrial.store.connection.mongo.details.impl

class NoAuthMongoDetails(val host: String = "127.0.0.1", val port: Int = 27017) : AbstractMongoDetail() {
    override fun getURI(): String {
        return "mongodb://${host}:${port}"
    }
}