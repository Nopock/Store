package org.hyrical.store.repository.impl.flatfile

import com.google.gson.reflect.TypeToken
import org.hyrical.store.DataStoreController
import org.hyrical.store.Storable
import org.hyrical.store.connection.flatfile.FlatFileConnection
import org.hyrical.store.repository.AsyncRepository
import org.hyrical.store.serializers.Serializers
import java.io.File
import java.util.concurrent.CompletableFuture

class AsyncFlatFileRepository<T : Storable>(controller: DataStoreController<T>, val connection: FlatFileConnection) : AsyncRepository<T> {

    val file: File = File(controller.directory, controller.classType.simpleName + ".json").also {
        if (!it.exists()) it.createNewFile()
    }

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

    /**
     * @param id The ID of the [T] object that you are searching for.
     *
     * @return [CompletableFuture<T>] The [T] object wrapped in CompletableFuture if found else null
     */
    override fun search(id: String): CompletableFuture<T?> {
        return CompletableFuture.supplyAsync {
            cache[id]
        }
    }

    /**
     * @param id The ID of the [T] object to delete.
     */
    override fun delete(id: String) {
        CompletableFuture.runAsync {
            cache.remove(id)
            persistToFile()
        }
    }

    /**
     * @param keys A vararg of keys/ids that will be deleted.
     */
    override fun deleteMany(vararg keys: String) {
        CompletableFuture.runAsync {
            keys.forEach { key -> cache.remove(key) }
            persistToFile()
        }
    }

    /**
     * @return [CompletableFuture<List<T>>] A list of all the objects in the repository.
     */
    override fun findAll(): CompletableFuture<List<T>> {
        return CompletableFuture.supplyAsync {
            cache.values.toList()
        }
    }

    /**
     * @param objects A vararg of [T]'s that need to be saved.
     *
     * @return [CompletableFuture<List<T>>] A list of the objects saved.
     */
    override fun saveMany(vararg objects: T): CompletableFuture<List<T>> {
        return CompletableFuture.supplyAsync {
            objects.forEach { obj -> cache[obj.identifier] = obj }
            persistToFile()
            objects.toList()
        }
    }

    /**
     * @param t The object to save.
     *
     * @return [CompletableFuture<T>] The saved object wrapped in a CompletableFuture.
     */
    override fun save(t: T): CompletableFuture<T> {
        return CompletableFuture.supplyAsync {
            cache[t.identifier] = t
            persistToFile()
            t
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