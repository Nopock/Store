package org.hyrical.store

import org.hyrical.store.connection.DatabaseConnection
import org.hyrical.store.repository.AsyncRepository
import org.hyrical.store.repository.CoroutineRepository
import org.hyrical.store.repository.ReactiveRepository
import org.hyrical.store.repository.Repository
import org.hyrical.store.type.StorageType

/**
 * The object that handles creating new [Repository]'s and
 * [AsyncRepository]'s.
 *
 * @param [T] An objects that implements [Storable] (The type of data to be stored)
 * @param [type] The type of [StorageType] to be used
 *
 * @author Nopox
 * @since 11/10/22
 */
class DataStoreController<T : Storable>(private val type: StorageType, val classType: Class<T>, val connection: DatabaseConnection<*, *>?) {

    companion object {

        /**
         * Creates a new instance of the [DataStoreController] with
         * the specified [StorageType]
         *
         * @param [type] An objects that implements [Storable] (The type of data to be stored)
         *
         * @see [DataStoreController]
         */
        inline fun <reified T : Storable> of(type: StorageType, connection: DatabaseConnection<*, *>? = null): DataStoreController<T> {
            return DataStoreController(type, T::class.java, connection)
        }

        @JvmStatic
        fun <T : Storable> of(type: StorageType, t: Class<T>, connection: DatabaseConnection<*, *>? = null): DataStoreController<T> {
            return DataStoreController(type, t, connection)
        }
    }

    // We do this lazy cause maybe they don't use a repo only async or reactive
    val repository: Repository<T> by lazy {
        type.build(this, connection)
    }

    val asyncRepository: AsyncRepository<T> by lazy {
        type.buildAsync(this, connection)
    }

    val reactiveRepository: ReactiveRepository<T> by lazy {
        type.buildReactive(this, connection)
    }

    val coroutineRepository: CoroutineRepository<T> by lazy {
        type.buildCoroutine(this, connection)
    }

    var directory: String = ""

    /**
     * Sets the directory to be used when persisting data
     * with the [StorageType.FLAT_FILE]
     *
     * @param [directory] The directory to be used.
     */
    fun bindFlatFileDirectory(directory: String)  {
        if (type == StorageType.FLAT_FILE) {
            this.directory = directory
        } else {
            throw UnsupportedOperationException("You attempted to bind a flat file directory to a non flat file DataStoreController!!")
        }
    }
}