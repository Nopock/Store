package org.hyrical.store.repository.impl.flatfile

import org.hyrical.store.DataStoreController
import org.hyrical.store.Storable
import org.hyrical.store.constants.DataTypeResources
import org.hyrical.store.repository.AsyncRepository
import java.io.File
import java.util.concurrent.CompletableFuture

class AsyncFlatFileRepository<T : Storable>(controller: DataStoreController<T>) : AsyncRepository<T> {

    val file: File = File(controller.directory, controller.classType.simpleName + ".json").also {
        if (!it.exists()) it.createNewFile()
    }

    override fun search(id: String): CompletableFuture<T?> {
        throw NotImplementedError()
    }

    override fun delete(id: String) {
        throw NotImplementedError()
    }

    override fun deleteMany(vararg keys: String) {
        throw NotImplementedError()
    }

    /**
     * @return [List<T>] A list of all the objects in the repository.
     */
    override fun findAll(): CompletableFuture<List<T>> {
        TODO("Not yet implemented")
    }

    override fun saveMany(vararg objects: T): CompletableFuture<List<T>> {
        throw NotImplementedError()
    }

    override fun save(t: T): CompletableFuture<T> {
        throw NotImplementedError()
    }
}