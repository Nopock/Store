package org.hyrical.store

import org.hyrical.store.caching.CachingStrategy
import org.hyrical.store.caching.ICachingStrategy
import org.hyrical.store.connection.DatabaseConnection
import org.hyrical.store.repository.AsyncRepository
import org.hyrical.store.repository.Repository
import org.hyrical.store.type.StorageType
import java.lang.reflect.ParameterizedType
import java.util.UUID

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
         * the specified [CachingStrategy] and [StorageType]
         *
         * @param [type] An objects that implements [Storable] (The type of data to be stored)
         * @param [cachingStrategy] The [CachingStrategy] to be used whilst persisting data. (Defaulted to [CachingStrategy.NONE]
         *
         * @see [DataStoreController]
         */
        inline fun <reified T : Storable> of(type: StorageType, connection: DatabaseConnection<*, *>? = null, cachingStrategy: CachingStrategy = CachingStrategy.NONE): DataStoreController<T> {
            return DataStoreController(type, T::class.java, connection).apply {
                enableCachingStrategy(cachingStrategy)
            }
        }

        @JvmStatic
        fun <T : Storable> of(type: StorageType, t: Class<T>, cachingStrategy: CachingStrategy = CachingStrategy.NONE): DataStoreController<T> {
            return DataStoreController<T>(type, t, null).apply {
                enableCachingStrategy(cachingStrategy)
            }
        }
    }

    // We do this lazy cause maybe they don't use a repo only async or reactive
    val repository: Repository<T> by lazy {
        type.build(this)
    }

    val asyncRepository: AsyncRepository<T> by lazy {
        type.buildAsync(this)
    }

    private var cachingStrategy: CachingStrategy = CachingStrategy.NONE
    private var cache: ICachingStrategy<T>? = null
    var directory: String = ""

    /**
     * Enables a certain [CachingStrategy] to be used whilst persisting
     * data.
     *
     * @param [cachingStrategy] The [CachingStrategy] to be used.
     */
    fun enableCachingStrategy(cachingStrategy: CachingStrategy) {
        this.cachingStrategy = cachingStrategy
    }

    /**
     * Sets the directory to be used when persisting data
     * with the [StorageType.FLAT_FILE]
     *
     * @param [directory] The directory to be used.
     */
    fun bindFlatFileDirectory(directory: String)  {
        if (type == StorageType.FLAT_FILE) {
            this.directory = directory
        }
    }
}