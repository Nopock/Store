package org.hyrical.store.repository.impl.flatfile

import org.hyrical.store.DataStoreController
import org.hyrical.store.Storable
import org.hyrical.store.repository.AsyncRepository
import org.hyrical.store.serializers.Serializers
import java.io.File
import java.util.concurrent.CompletableFuture

class AsyncFlatFileRepository<T : Storable>(controller: DataStoreController<T>) : AsyncRepository<T> {

    val file: File = File(controller.directory, controller.classType.simpleName + ".json").also {
        if (!it.exists()) it.createNewFile()
    }

    val cache: MutableList<T> = mutableListOf()

    init {
        for (line in file.readLines()) {
            cache.add(Serializers.activeSerialize.deserialize(line, controller.classType)!!)
        }
    }

    /**
     * @param id The ID of the [T] object that you are searching for.
     *
     * @return [CompletableFuture<T>] The [T] object wrapped in CompletableFuture if found else null
     */
    override fun search(id: String): CompletableFuture<T?> {
        return CompletableFuture.supplyAsync {
            cache.firstOrNull { it.identifier == id }
        }
    }

    /**
     * @param id The ID of the [T] object to delete.
     */
    override fun delete(id: String) {
        cache.removeIf { it.identifier == id }
        file.writeText(Serializers.activeSerialize.serialize(cache)!!)
    }

    /**
     * @param keys A vararg of keys/ids that will be deleted.
     */
    override fun deleteMany(vararg keys: String) {
        cache.removeIf { keys.contains(it.identifier) }
        file.writeText(Serializers.activeSerialize.serialize(cache)!!)
    }

    /**
     * @return [CompletableFuture<List<T>>] A list of all the objects in the repository.
     */
    override fun findAll(): CompletableFuture<List<T>> {
        return CompletableFuture.supplyAsync {
            cache
        }
    }

    /**
     * @param objects A vararg of [T]'s that need to be saved.
     *
     * @return [CompletableFuture<List<T>>] A list of the objects saved.
     */
    override fun saveMany(vararg objects: T): CompletableFuture<List<T>> {
        return CompletableFuture.supplyAsync {
            cache.addAll(objects)
            file.writeText(Serializers.activeSerialize.serialize(cache)!!)
            cache
        }
    }

    /**
     * @param t The object to save.
     *
     * @return [CompletableFuture<T>] The saved object wrapped in a CompletableFuture.
     */
    override fun save(t: T): CompletableFuture<T> {
        return CompletableFuture.supplyAsync {
            cache.add(t)
            file.writeText(Serializers.activeSerialize.serialize(cache)!!)
            t
        }
    }
}