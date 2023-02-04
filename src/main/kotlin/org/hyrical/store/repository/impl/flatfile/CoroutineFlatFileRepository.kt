package org.hyrical.store.repository.impl.flatfile

import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.hyrical.store.DataStoreController
import org.hyrical.store.Storable
import org.hyrical.store.connection.flatfile.FlatFileConnection
import org.hyrical.store.repository.CoroutineRepository
import org.hyrical.store.serializers.Serializers

class CoroutineFlatFileRepository<T : Storable>(controller: DataStoreController<T>, val connection: FlatFileConnection) : CoroutineRepository<T> {

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

    override suspend fun search(id: String): T? {
        return cache[id]
    }

    override suspend fun save(t: T): T {
        return withContext(Dispatchers.IO) {
            cache[t.identifier] = t
            persistToFile()
            t
        }
    }

    override suspend fun delete(id: String) {
        withContext(Dispatchers.IO) {
            cache.remove(id)
            persistToFile()
        }
    }

    override suspend fun saveMany(vararg objects: T): List<T> {
        return withContext(Dispatchers.IO) {
            objects.forEach { obj -> cache[obj.identifier] = obj }
            persistToFile()
            objects.toList()
        }
    }

    override suspend fun deleteMany(vararg keys: String) {
        withContext(Dispatchers.IO) {
            keys.forEach { key -> cache.remove(key) }
            persistToFile()
        }
    }

    override suspend fun findAll(): List<T> {
        return withContext(Dispatchers.IO) {
            cache.values.toList()
        }
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