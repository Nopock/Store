package org.hyrical.store.connection.mongo

import com.mongodb.MongoClient
import com.mongodb.MongoClientURI
import com.mongodb.client.MongoDatabase
import org.hyrical.store.connection.DatabaseConnection
import org.hyrical.store.connection.mongo.details.AbstractMongoDetail

class MongoConnection(val details: AbstractMongoDetail) : DatabaseConnection<MongoClient, MongoDatabase> {

    lateinit var handle: MongoClient

    override fun useResource(lambda: MongoDatabase.() -> Unit) {
        val applied = getAppliedResource()
        lambda.invoke(applied)
    }

    override fun <T> useResourceWithReturn(lambda: MongoDatabase.() -> T): T {
        return lambda.invoke(
            getAppliedResource()
        )
    }

    override fun setConnection(connection: MongoClient) {
        handle = connection
    }

    fun getAppliedResource(): MongoDatabase {
        return try {
            getConnection().getDatabase(details.database)
        } catch (ignored: Exception) {
            setConnection(createNewConnection())

            getConnection().getDatabase(details.database)
        }
    }

    override fun getConnection(): MongoClient {
        return try {
            handle
        } catch (e: Exception) {
            createNewConnection()
        }
    }

    override fun createNewConnection(): MongoClient {
        return MongoClient(
            MongoClientURI(details.getURI())
        )
    }
}