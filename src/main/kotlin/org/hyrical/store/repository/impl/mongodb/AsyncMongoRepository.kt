package org.hyrical.store.repository.impl.mongodb

import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters
import org.bson.Document
import org.hyrical.store.DataStoreController
import org.hyrical.store.Storable
import org.hyrical.store.constants.DataTypeResources
import org.hyrical.store.repository.AsyncRepository
import org.hyrical.store.serializeres.Serializers
import java.lang.UnsupportedOperationException
import java.util.concurrent.CompletableFuture

class AsyncMongoRepository<T : Storable>(private val controller: DataStoreController<T>) : AsyncRepository<T> {

    private val id = controller.classType.simpleName

    private val serializer = Serializers.activeSerialize

    var collection: MongoCollection<Document> = if (DataTypeResources.mongoCollections.containsKey(id)) {
        DataTypeResources.mongoCollections[id]!!
    } else {
        val collection = DataTypeResources.mongoDatabase!!.getCollection(id)
        DataTypeResources.mongoCollections[id] = collection
        collection
    }

    override fun search(id: String): CompletableFuture<T?> {
        return CompletableFuture.supplyAsync {
            return@supplyAsync serializer.deserialize(collection.find(Filters.eq("_id", id)).first()?.toJson() ?: return@supplyAsync null, controller.classType)
        }
    }

    override fun delete(id: String) {
        CompletableFuture.runAsync {
            collection.deleteOne(Filters.eq("_id", id))
        }
    }

    override fun deleteMany(vararg keys: String) {
        CompletableFuture.runAsync {
            collection.deleteMany(Filters.`in`("_id", keys))
        }
    }

    /**
     * @return [List<T>] A list of all the objects in the repository.
     */
    override fun findAll(): CompletableFuture<List<T>> {
        return CompletableFuture.supplyAsync {
            return@supplyAsync collection.find().map { serializer.deserialize(it.toJson(), controller.classType)!! }.toList()
        }
    }

    override fun saveMany(vararg objects: T): CompletableFuture<List<T>> {
        return CompletableFuture.supplyAsync {
            objects.forEach {
                save(it)
            }

            return@supplyAsync objects.toList()
        }
    }

    override fun save(t: T): CompletableFuture<T> {
        return CompletableFuture.supplyAsync {
            collection.updateOne(Filters.eq("_id", t.identifier), Document("\$set",  Document.parse(serializer.serialize(t))))

            return@supplyAsync t
        }
    }
}