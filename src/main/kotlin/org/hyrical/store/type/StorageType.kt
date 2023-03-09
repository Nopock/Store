/*
 * MIT License
 *
 * Copyright (c) 2023 Nathan
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.hyrical.store.type

import org.hyrical.store.DataStoreController
import org.hyrical.store.Storable
import org.hyrical.store.connection.DatabaseConnection
import org.hyrical.store.connection.flatfile.FlatFileConnection
import org.hyrical.store.connection.mongo.MongoConnection
import org.hyrical.store.connection.redis.RedisConnection
import org.hyrical.store.repository.AsyncRepository
import org.hyrical.store.repository.CoroutineRepository
import org.hyrical.store.repository.ReactiveRepository
import org.hyrical.store.repository.Repository
import org.hyrical.store.repository.impl.flatfile.AsyncFlatFileRepository
import org.hyrical.store.repository.impl.flatfile.CoroutineFlatFileRepository
import org.hyrical.store.repository.impl.flatfile.FlatFileRepository
import org.hyrical.store.repository.impl.flatfile.ReactiveFlatFileRepository
import org.hyrical.store.repository.impl.mongodb.AsyncMongoRepository
import org.hyrical.store.repository.impl.mongodb.CoroutineMongoRepository
import org.hyrical.store.repository.impl.mongodb.MongoRepository
import org.hyrical.store.repository.impl.mongodb.ReactiveMongoRepository
import org.hyrical.store.repository.impl.redis.AsyncRedisRepository
import org.hyrical.store.repository.impl.redis.CoroutineRedisRepository
import org.hyrical.store.repository.impl.redis.ReactiveRedisRepository
import org.hyrical.store.repository.impl.redis.RedisRepository

/**
 * The type of storage to be used whilst persisting data.
 *
 * @author Nopox
 * @since 11/10/22
 */
enum class StorageType {

	MONGO() {
		override fun <T : Storable> build(
			controller: DataStoreController<T>,
			connection: DatabaseConnection<*, *>?
		): Repository<T> {
			return MongoRepository(controller, connection as MongoConnection)
		}

		override fun <T : Storable> buildAsync(
			controller: DataStoreController<T>,
			connection: DatabaseConnection<*, *>?
		): AsyncRepository<T> {
			return AsyncMongoRepository(controller, connection as MongoConnection)
		}

		override fun <T : Storable> buildReactive(
			controller: DataStoreController<T>,
			connection: DatabaseConnection<*, *>?
		): ReactiveRepository<T> {
			return ReactiveMongoRepository(controller, connection as MongoConnection)
		}

		override fun <T : Storable> buildCoroutine(
			controller: DataStoreController<T>,
			connection: DatabaseConnection<*, *>?
		): CoroutineRepository<T> {
			return CoroutineMongoRepository(controller, connection as MongoConnection)
		}
	},

	REDIS() {
		override fun <T : Storable> build(
			controller: DataStoreController<T>,
			connection: DatabaseConnection<*, *>?
		): Repository<T> {
			return RedisRepository(controller, connection as RedisConnection)
		}

		override fun <T : Storable> buildAsync(
			controller: DataStoreController<T>,
			connection: DatabaseConnection<*, *>?
		): AsyncRepository<T> {
			return AsyncRedisRepository(controller, connection as RedisConnection)
		}

		override fun <T : Storable> buildReactive(
			controller: DataStoreController<T>,
			connection: DatabaseConnection<*, *>?
		): ReactiveRepository<T> {
			return ReactiveRedisRepository(controller, connection as RedisConnection)
		}

		override fun <T : Storable> buildCoroutine(
			controller: DataStoreController<T>,
			connection: DatabaseConnection<*, *>?
		): CoroutineRepository<T> {
			return CoroutineRedisRepository(controller, connection as RedisConnection)
		}
	},

	FLAT_FILE() {
		override fun <T : Storable> build(
			controller: DataStoreController<T>,
			connection: DatabaseConnection<*, *>?
		): Repository<T> {
			return FlatFileRepository(controller, connection as FlatFileConnection)
		}

		override fun <T : Storable> buildAsync(
			controller: DataStoreController<T>,
			connection: DatabaseConnection<*, *>?
		): AsyncRepository<T> {
			return AsyncFlatFileRepository(controller, connection as FlatFileConnection)
		}

		override fun <T : Storable> buildReactive(
			controller: DataStoreController<T>,
			connection: DatabaseConnection<*, *>?
		): ReactiveRepository<T> {
			return ReactiveFlatFileRepository(controller, connection as FlatFileConnection)
		}

		override fun <T : Storable> buildCoroutine(
			controller: DataStoreController<T>,
			connection: DatabaseConnection<*, *>?
		): CoroutineRepository<T> {
			return CoroutineFlatFileRepository(controller, connection as FlatFileConnection)
		}
	};

	/**
	 * Builds and initiates the [Repository]
	 *
	 * @param [controller] The owning [DataStoreController]
	 */
	abstract fun <T : Storable> build(
		controller: DataStoreController<T>,
		connection: DatabaseConnection<*, *>?
	): Repository<T>

	/**
	 * Builds and initiates the [AsyncRepository]
	 *
	 * @param [controller] The owning [DataStoreController]
	 */
	abstract fun <T : Storable> buildAsync(
		controller: DataStoreController<T>,
		connection: DatabaseConnection<*, *>?
	): AsyncRepository<T>

	/**
	 * Builds and initiates the [ReactiveRepository]
	 *
	 * @param [controller] The owning [DataStoreController]
	 */
	abstract fun <T : Storable> buildReactive(
		controller: DataStoreController<T>,
		connection: DatabaseConnection<*, *>?
	): ReactiveRepository<T>

	/**
	 * Builds and initiates the [ReactiveRepository]
	 *
	 * @param [controller] The owning [DataStoreController]
	 */
	abstract fun <T : Storable> buildCoroutine(
		controller: DataStoreController<T>,
		connection: DatabaseConnection<*, *>?
	): CoroutineRepository<T>
}
