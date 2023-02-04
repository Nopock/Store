package org.hyrical.store.repository

import org.hyrical.store.DataStoreController
import org.hyrical.store.Storable

/**
 * The base repository for all [Storable] objects,
 * initiated by a [DataStoreController]
 *
 * @author Nopox
 * @since 2/3/23
 */
interface CoroutineRepository<T : Storable> {

    /**
     * @param [id] The ID of the [T] object that you are searching for.
     *
     * @return [T?] The [T] object if found else null.
     */
    suspend fun search(id: String): T?

    /**
     * @param [t] The object to save.
     *
     * @return [T] The object saved.
     */
    suspend fun save(t: T): T

    /**
     * @param [id] The ID of the [T] object to delete.
     */
    suspend fun delete(id: String)

    /**
     * @param [objects] A vararg of [T]'s that need to be saved.
     *
     * @return [List<T>] A list of the objects saved.
     */
    suspend fun saveMany(vararg objects: T): List<T>

    /**
     * @param [keys] A vararg of keys/ids that will be deleted.
     */
    suspend fun deleteMany(vararg keys: String)

    /**
     * @return [List<T>] A list of all the objects in the repository.
     */
    suspend fun findAll(): List<T>
}