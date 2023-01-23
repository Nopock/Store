package org.hyrical.store.connection.mongo.details.impl

import org.hyrical.store.connection.mongo.details.AbstractMongoDetail

class NoAuthMongoDetails(
    val host: String = "127.0.0.1",
    val port: Int = 27017
    ) : AbstractMongoDetail() {
    
    override fun getURI(): String {
        return "mongodb://${host}:${port}"
    }
}