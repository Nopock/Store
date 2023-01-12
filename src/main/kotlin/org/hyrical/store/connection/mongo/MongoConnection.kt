package org.hyrical.store.connection.mongo

class MongoCollection(val details: MongoURIDetails) : DatabaseConnection<MongoClient, MongoDatabase> {

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

    override fun close() {
        handle.close()
    }

    override fun getAppliedResource(): MongoDatabase {
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
            MongoClientURI(details.uri)
        )
    }
}