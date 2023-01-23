package org.hyrical.store.connection.mongo.details.impl

class URIMongoDetails(val uri: String, database: String) : AbstractMongoDetail(database) {

    override fun getURI(): String {
        return uri
    }
}