package org.hyrical.store

import org.hyrical.store.caching.CachingStrategy
import org.hyrical.store.caching.ICachingStrategy
import org.hyrical.store.constants.DataTypeResources
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
class DataStoreController<T : Storable>(private val type: StorageType, val classType: Class<T>) {

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
        inline fun <reified T : Storable> of(type: StorageType, cachingStrategy: CachingStrategy = CachingStrategy.NONE): DataStoreController<T> {
            return DataStoreController(type, T::class.java).apply {
                enableCachingStrategy(cachingStrategy)
            }
        }
    }

    lateinit var repository: Repository<T>
    lateinit var asyncRepository: AsyncRepository<T>

    var cachingStrategy: CachingStrategy = CachingStrategy.NONE
    var cache: ICachingStrategy<T>? = null
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

    /**
     * Constructs the current [DataStoreController] object and
     * initializes the [Repository] and the [AsyncRepository] for the
     * current [StorageType]
     */
    fun construct() {
        if (this.cache != null) throw java.lang.UnsupportedOperationException("This controller has already been constructed.")

        this.repository = type.build(this)
        this.asyncRepository = type.buildAsync(this)

        this.cache = cachingStrategy.constructCache()
    }
}