package org.hyrical.store.repository.impl.flatfile

import org.hyrical.store.DataStoreController
import org.hyrical.store.Storable
import org.hyrical.store.repository.Repository
import org.hyrical.store.serializers.Serializers
import java.io.File
import java.util.ArrayList
import com.google.gson.reflect.TypeToken
import org.hyrical.store.connection.flatfile.FlatFileConnection

class FlatFileRepository<T : Storable>(controller: DataStoreController<T>, val connection: FlatFileConnection) : Repository<T> {

    val cache = mutableMapOf<String, T>()
    
    init {
        // Read the file and deserialize the contents into the cache map
        val jsonString = connection.useResourceWithReturn {
            readText()
        }
        val type = TypeToken.getParameterized(ArrayList::class.java, controller.classType).type
        val objects = Serializers.activeSerializer.deserialize<ArrayList<T>>(jsonString, type)
        objects?.forEach { obj -> cache[obj.identifier] = obj }   
    }

    override fun search(id: String): T? {
        return cache[id]
    }

    override fun save(t: T): T {
        cache[t.identifier] = t
        persistToFile()
        return t
    }

    override fun delete(id: String) {
        cache.remove(id)
        persistToFile()
    }

    override fun saveMany(vararg objects: T): List<T> {
        objects.forEach { obj -> cache[obj.identifier] = obj }
        persistToFile()
        return objects.toList()
    }

    override fun deleteMany(vararg keys: String) {
        keys.forEach { key -> cache.remove(key) }
        persistToFile()
    }

    override fun findAll(): List<T> {
        return cache.values.toList()
    }

    private fun persistToFile() {
        // Serialize the cache map and write it to the file
        val jsonString = Serializers.activeSerializer.serialize(cache.values)

        connection.useResource {
            if (jsonString != null) {
                writeText(jsonString)
            }
        }
    }
}