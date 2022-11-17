package org.hyrical.store.repository.impl.mongodb

import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import org.bson.Document
import org.hyrical.store.DataStoreController
import org.hyrical.store.Storable
import org.hyrical.store.constants.DataTypeResources
import org.hyrical.store.repository.Repository
import org.hyrical.store.serializers.Serializers
import java.util.logging.Logger

class MongoRepository<T : Storable>(private val controller: DataStoreController<T>) : Repository<T> {

    private val id = controller.classType.simpleName

    private val serializer = Serializers.activeSerialize

    var collection: MongoCollection<Document> = if (DataTypeResources.mongoCollections.containsKey(id)) {
        DataTypeResources.mongoCollections[id]!!
    } else {
        val collection = DataTypeResources.mongoDatabase!!.getCollection(id)
        DataTypeResources.mongoCollections[id] = collection
        collection
    }

    override fun search(id: String): T? {
        val s = collection.find(Filters.eq("_id", id)).first() ?: return null

        return serializer.deserialize(s.toJson(), controller.classType)
    }

    override fun delete(id: String) {
        collection.deleteOne(Filters.eq("_id", id))
    }

    override fun deleteMany(vararg keys: String) {
        collection.deleteMany(Filters.`in`("_id", keys))
    }

    /**
     * @return [List<T>] A list of all the objects in the repository.
     */
    override fun findAll(): List<T> {
        return collection.find().map { serializer.deserialize(it.toJson(), controller.classType)!! }.toList()
    }

    override fun saveMany(vararg objects: T): List<T> {
        objects.forEach {
            save(it)
        }
        return objects.toList()
    }

    override fun save(t: T): T {
        collection.updateOne(Filters.eq("_id", t.identifier), Document("\$set",  Document.parse(serializer.serialize(t))), UpdateOptions().upsert(true))
        return t
    }
}