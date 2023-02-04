package org.hyrical.store.repository

import org.hyrical.store.DataStoreController
import org.hyrical.store.Storable
import java.util.concurrent.CompletableFuture

/**
 * The base repository for all [Storable] objects,
 * initiated by a [DataStoreController]
 *
 * @author Nopox
 * @since 1/12/2023
 */
interface AsyncRepository<T : Storable> {

    /**
     * @param id The ID of the [T] object that you are searching for.
     *
     * @return [CompletableFuture<T>] The [T] object wrapped in CompletableFuture if found else null
     */
    fun search(id: String): CompletableFuture<T?>

    /**
     * @param t The object to save.
     *
     * @return [CompletableFuture<T>] The saved object wrapped in a CompletableFuture.
     */
    fun save(t: T): CompletableFuture<T>

    /**
     * @param id The ID of the [T] object to delete.
     */
    fun delete(id: String)

    /**
     * @param objects A vararg of [T]'s that need to be saved.
     *
     * @return [CompletableFuture<List<T>>] A list of the objects saved.
     */
    fun saveMany(vararg objects: T): CompletableFuture<List<T>>


    /**
     * @param keys A vararg of keys/ids that will be deleted.
     */
    fun deleteMany(vararg keys: String)

    /**
     * @return [CompletableFuture<List<T>>] A list of all the objects in the repository.
     */
    fun findAll(): CompletableFuture<List<T>>
}