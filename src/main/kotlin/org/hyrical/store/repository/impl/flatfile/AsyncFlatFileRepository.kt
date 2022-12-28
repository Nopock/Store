package org.hyrical.store.repository.impl.flatfile

import org.hyrical.store.DataStoreController
import org.hyrical.store.Storable
import org.hyrical.store.constants.DataTypeResources
import org.hyrical.store.repository.AsyncRepository
import org.hyrical.store.serializers.Serializers
import java.io.File
import java.util.concurrent.CompletableFuture

class AsyncFlatFileRepository<T : Storable>(controller: DataStoreController<T>) : AsyncRepository<T> {

    val file: File = File(controller.directory, controller.classType.simpleName + ".json").also {
        if (!it.exists()) it.createNewFile()
    }

    val cache: MutableList<T> = mutableListOf()

    override fun search(id: String): CompletableFuture<T?> {
        return CompletableFuture.supplyAsync {
            cache.firstOrNull { it.identifier == id }
        }
    }

    override fun delete(id: String) {
        cache.removeIf { it.identifier == id }
        file.writeText(Serializers.activeSerialize.serialize(cache)!!)
    }

    override fun deleteMany(vararg keys: String) {
        cache.removeIf { keys.contains(it.identifier) }
        file.writeText(Serializers.activeSerialize.serialize(cache)!!)
    }

    /**
     * @return [List<T>] A list of all the objects in the repository.
     */
    override fun findAll(): CompletableFuture<List<T>> {
        return CompletableFuture.supplyAsync {
            cache
        }
    }

    override fun saveMany(vararg objects: T): CompletableFuture<List<T>> {
        return CompletableFuture.supplyAsync {
            cache.addAll(objects)
            file.writeText(Serializers.activeSerialize.serialize(cache)!!)
            cache
        }
    }

    override fun save(t: T): CompletableFuture<T> {
        return CompletableFuture.supplyAsync {
            cache.add(t)
            file.writeText(Serializers.activeSerialize.serialize(cache)!!)
            t
        }
    }
}