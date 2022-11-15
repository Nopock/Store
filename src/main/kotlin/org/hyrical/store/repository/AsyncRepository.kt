package org.hyrical.store.repository

import org.hyrical.store.Storable
import java.util.concurrent.CompletableFuture

interface AsyncRepository<T : Storable> {
    fun search(id: String): CompletableFuture<T?>

    fun save(t: T): CompletableFuture<T>

    fun delete(id: String)

    fun saveMany(vararg objects: T): CompletableFuture<List<T>>

    fun deleteMany(vararg keys: String)

    /**
     * @return [List<T>] A list of all the objects in the repository.
     */
    fun findAll(): CompletableFuture<List<T>>
}