package org.hyrical.store.type

import org.hyrical.store.DataStoreController
import org.hyrical.store.Storable
import org.hyrical.store.connection.DatabaseConnection
import org.hyrical.store.connection.flatfile.FlatFileConnection
import org.hyrical.store.connection.mongo.MongoConnection
import org.hyrical.store.connection.redis.RedisConnection
import org.hyrical.store.repository.AsyncRepository
import org.hyrical.store.repository.ReactiveRepository
import org.hyrical.store.repository.Repository
import org.hyrical.store.repository.impl.flatfile.AsyncFlatFileRepository
import org.hyrical.store.repository.impl.flatfile.FlatFileRepository
import org.hyrical.store.repository.impl.flatfile.ReactiveFlatFileRepository
import org.hyrical.store.repository.impl.mongodb.AsyncMongoRepository
import org.hyrical.store.repository.impl.mongodb.MongoRepository
import org.hyrical.store.repository.impl.mongodb.ReactiveMongoRepository
import org.hyrical.store.repository.impl.redis.AsyncRedisRepository
import org.hyrical.store.repository.impl.redis.RedisRepository
import org.hyrical.store.repository.impl.redis.ReactiveRedisRepository

/**
 * The type of storage to be used whilst persisting data.
 *
 * @author Nopox
 * @since 11/10/22
 */
enum class StorageType {

    MONGO() {
        override fun <T : Storable> build(controller: DataStoreController<T>, connection: DatabaseConnection<*, *>?): Repository<T> {
            return MongoRepository(controller, connection as MongoConnection)
        }

        override fun <T : Storable> buildAsync(controller: DataStoreController<T>, connection: DatabaseConnection<*, *>?): AsyncRepository<T> {
            return AsyncMongoRepository(controller, connection as MongoConnection)
        }

        override fun <T : Storable> buildReactive(controller: DataStoreController<T>, connection: DatabaseConnection<*, *>?): ReactiveRepository<T> {
            return ReactiveMongoRepository(controller, connection as MongoConnection)
        }
    },

    REDIS() {
        override fun <T : Storable> build(controller: DataStoreController<T>, connection: DatabaseConnection<*, *>?): Repository<T> {
            return RedisRepository(controller, connection as RedisConnection)
        }

        override fun <T : Storable> buildAsync(controller: DataStoreController<T>, connection: DatabaseConnection<*, *>?): AsyncRepository<T> {
            return AsyncRedisRepository(controller, connection as RedisConnection)
        }

        override fun <T : Storable> buildReactive(controller: DataStoreController<T>, connection: DatabaseConnection<*, *>?): ReactiveRepository<T> {
            return ReactiveRedisRepository(controller, connection as RedisConnection)
        }
    },

    FLAT_FILE() {
        override fun <T : Storable> build(controller: DataStoreController<T>, connection: DatabaseConnection<*, *>?): Repository<T> {
            return FlatFileRepository(controller, connection as FlatFileConnection)
        }

        override fun <T : Storable> buildAsync(controller: DataStoreController<T>, connection: DatabaseConnection<*, *>?): AsyncRepository<T> {
            return AsyncFlatFileRepository(controller, connection as FlatFileConnection)
        }

        override fun <T : Storable> buildReactive(controller: DataStoreController<T>, connection: DatabaseConnection<*, *>?): ReactiveRepository<T> {
            return ReactiveFlatFileRepository(controller, connection as FlatFileConnection)
        }
    };

    /**
     * Builds and initiates the [Repository]
     *
     * @param [controller] The owning [DataStoreController]
     */
    abstract fun <T : Storable> build(controller: DataStoreController<T>, connection: DatabaseConnection<*, *>?): Repository<T>

    /**
     * Builds and initiates the [AsyncRepository]
     *
     * @param [controller] The owning [DataStoreController]
     */
    abstract fun <T : Storable> buildAsync(controller: DataStoreController<T>, connection: DatabaseConnection<*, *>?): AsyncRepository<T>

    /**
     * Builds and initiates the [ReactiveRepository]
     *
     * @param [controller] The owning [DataStoreController]
     */
    abstract fun <T : Storable> buildReactive(controller: DataStoreController<T>, connection: DatabaseConnection<*, *>?): ReactiveRepository<T>
}