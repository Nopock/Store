package org.hyrical.store.type

import org.hyrical.store.DataStoreController
import org.hyrical.store.Storable
import org.hyrical.store.repository.*

/**
 * The type of storage to be used whilst persisting data.
 *
 * @author Nopox
 * @since 11/10/22
 */
enum class StorageType {

    MONGO() {
        override fun <T : Storable> build(controller: DataStoreController<T>): Repository<T> {
            return MongoRepository(controller)
        }

        override fun <T : Storable> buildAsync(controller: DataStoreController<T>): AsyncRepository<T> {
            return AsyncMongoRepository(controller)
        }

        override fun <T : Storable> buildReactive(controller: DataStoreController<T>): ReactiveRepository<T> {
            return ReactiveMongoRepository(controller)
        }
    },

    REDIS() {
        override fun <T : Storable> build(controller: DataStoreController<T>): Repository<T> {
            return RedisRepository(controller)
        }

        override fun <T : Storable> buildAsync(controller: DataStoreController<T>): AsyncRepository<T> {
            return AsyncRedisRepository(controller)
        }

        override fun <T : Storable> buildReactive(controller: DataStoreController<T>): ReactiveRepository<T> {
            return ReactiveRedisRepository(controller)
        }
    },

    FLAT_FILE() {
        override fun <T : Storable> build(controller: DataStoreController<T>): Repository<T> {
            return FlatFileRepository(controller)
        }

        override fun <T : Storable> buildAsync(controller: DataStoreController<T>): AsyncRepository<T> {
            return AsyncFlatFileRepository(controller)
        }

        override fun <T : Storable> buildReactive(controller: DataStoreController<T>): ReactiveRepository<T> {
            return ReactiveFlatFileRepository(controller)
        }
    };

    /**
     * Builds and initiates the [Repository]
     *
     * @param [controller] The owning [DataStoreController]
     */
    abstract fun <T : Storable> build(controller: DataStoreController<T>): Repository<T>

    /**
     * Builds and initiates the [AsyncRepository]
     *
     * @param [controller] The owning [DataStoreController]
     */
    abstract fun <T : Storable> buildAsync(controller: DataStoreController<T>): AsyncRepository<T>

    /**
     * Builds and initiates the [ReactiveRepository]
     *
     * @param [controller] The owning [DataStoreController]
     */
    abstract fun <T : Storable> buildReactive(controller: DataStoreController<T>): ReactiveRepository<T>
}