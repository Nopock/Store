package org.hyrical.store.repository.impl.mongodb

import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bson.Document
import org.hyrical.store.DataStoreController
import org.hyrical.store.Storable
import org.hyrical.store.connection.mongo.MongoConnection
import org.hyrical.store.repository.CoroutineRepository
import org.hyrical.store.serializers.Serializers

class CoroutineMongoRepository<T : Storable>(private val controller: DataStoreController<T>, val connection: MongoConnection) : CoroutineRepository<T> {

    private val collection: MongoCollection<Document> = connection.useResourceWithReturn {
        this.getCollection(controller.classType.simpleName)
    } ?: throw UnsupportedOperationException("You did not provide a mongodatabase connection when initiating the owning DataStoreContrller.")

    /**
     * @param [id] The ID of the [T] object that you are searching for.
     *
     * @return [T?] The [T] object if found else null.
     */
    override suspend fun search(id: String): T? {
        return withContext(Dispatchers.IO) {
            Serializers.activeSerializer.deserialize(collection.find(Filters.eq("_id", id)).first()?.toJson(), controller.classType)
        }
    }

    /**
     * @param [id] The ID of the [T] object to delete.
     */
    override suspend fun delete(id: String) {
        withContext(Dispatchers.IO) {
            collection.deleteOne(Filters.eq("_id", id))
        }
    }

    /**
     * @param [keys] A vararg of keys/ids that will be deleted.
     */
    override suspend fun deleteMany(vararg keys: String) {
        withContext(Dispatchers.IO) {
            collection.deleteMany(Filters.`in`("_id", keys))
        }
    }

    /**
     * @return [List<T>] A list of all the objects in the repository.
     */
    override suspend fun findAll(): List<T> {
        return withContext(Dispatchers.IO) {
            collection.find().map { Serializers.activeSerializer.deserialize(it.toJson(), controller.classType)!! }
                .toList()
        }
    }

    /**
     * @return [List<T>] A list of all the objects in the repository.
     */
    override suspend fun saveMany(vararg objects: T): List<T> {
        return withContext(Dispatchers.IO) {
            return@withContext objects.toList().onEach {
                save(it)
            }
        }
    }

    /**
     * @param [t] The object to save.
     *
     * @return [T] The object saved.
     */
    override suspend fun save(t: T): T {
        return withContext(Dispatchers.IO) {
            collection.updateOne(Filters.eq("_id", t.identifier), Document("\$set",  Document.parse(Serializers.activeSerializer.serialize(t))), UpdateOptions().upsert(true))
            return@withContext t
        }
    }
}