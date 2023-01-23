package org.hyrical.store.connection.mongo.details.impl

import org.hyrical.store.connection.mongo.details.AbstractMongoDetail

class URIMongoDetails(val uri: String) : AbstractMongoDetail() {

    override fun getURI(): String {
        return uri
    }
}