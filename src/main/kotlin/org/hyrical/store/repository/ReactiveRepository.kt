package org.hyrical.store.repository

/**
 * The base repository for all [Storable] objects,
 * initiated by a [DataStoreController]
 *
 * @author Nopox
 * @since 11/10/22
 */
interface Repository<T : Storable> {

    /**
     * @param [id] The ID of the [T] object that you are searching for.
     *
     * @return [T?] The [T] object if found else null.
     */
    fun search(id: String): Mono<T?>

    /**
     * @param [t] The object to save.
     *
     * @return [T] The object saved.
     */
    fun save(t: T): Mono<T>

    /**
     * @param [id] The ID of the [T] object to delete.
     */
    fun delete(id: String)

    /**
     * @param [objects] A vararg of [T]'s that need to be saved.
     *
     * @return [Flux<T>] A list of the objects saved.
     */
    fun saveMany(vararg objects: T): Flux<T>

    /**
     * @param [keys] A vararg of keys/ids that will be deleted.
     */
    fun deleteMany(vararg keys: String)

    /**
     * @return [Flux<T>] A list of all the objects in the repository.
     */
    fun findAll(): Flux<T>
}