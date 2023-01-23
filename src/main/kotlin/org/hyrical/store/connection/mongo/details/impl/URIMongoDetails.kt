package org.hyrical.store.connection.mongo.details.impl

class URIMongoDetails(val uri: String) : AbstractMongoDetail() {

    override fun getURI(): String {
        return uri
    }
}