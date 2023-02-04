package org.hyrical.store.connection.flatfile

import com.mongodb.client.MongoDatabase
import org.hyrical.store.connection.DatabaseConnection
import java.io.File

class FlatFileConnection(val directory: String, val fileName: String) : DatabaseConnection<File, File> {

    lateinit var handle: File

    override fun useResource(lambda: File.() -> Unit) {
        val applied = getAppliedResource()
        lambda.invoke(applied)
    }

    override fun <T> useResourceWithReturn(lambda: File.() -> T): T? {
        return lambda.invoke(
            getAppliedResource()
        )
    }

    override fun getConnection(): File {
        return try {
            handle
        } catch (e: Exception) {
            createNewConnection()
        }
    }

    override fun createNewConnection(): File {
        return File("$directory$fileName.json")
    }

    override fun setConnection(connection: File) {
        handle = connection
    }

    private fun getAppliedResource(): File {
        return try {
            getConnection()
        } catch (ignored: Exception) {
            setConnection(createNewConnection())

            getConnection()
        }
    }

}