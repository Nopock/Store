package org.hyrical.store.connection.mongo.details.impl

class AuthMongoDetails(
    val host: String = "127.0.0.1",
    val port: Int = 27017,
    val user: String,
    val password: String,
    database: String
) : AbstractMongoDetail(database) {
    override fun getURI(): String {
        return "mongodb://${user}:${password}@${host}:${port}"
    }
}