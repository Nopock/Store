package org.hyrical.store.connection.mongo

import com.mongodb.MongoClient
import com.mongodb.MongoClientURI
import com.mongodb.client.MongoDatabase
import org.hyrical.store.connection.DatabaseConnection
import org.hyrical.store.connection.mongo.details.AbstractMongoDetail

class MongoConnection(val details: AbstractMongoDetail, val database: String) : DatabaseConnection<MongoClient, MongoDatabase> {

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

    private fun getAppliedResource(): MongoDatabase {
        return try {
            getConnection().getDatabase(database)
        } catch (ignored: Exception) {
            setConnection(createNewConnection())

            getConnection().getDatabase(database)
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