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

package org.hyrical.store.repository.impl.redis

import org.hyrical.store.DataStoreController
import org.hyrical.store.Storable
import org.hyrical.store.connection.redis.RedisConnection
import org.hyrical.store.repository.AsyncRepository
import org.hyrical.store.serializers.Serializers
import java.util.concurrent.CompletableFuture

class AsyncRedisRepository<T : Storable>(
	private val controller: DataStoreController<T>,
	val connection: RedisConnection
) : AsyncRepository<T> {

	private val serializer = Serializers.activeSerializer

	private val id = controller.classType.simpleName

	/**
	 * @param id The ID of the [T] object that you are searching for.
	 *
	 * @return [CompletableFuture<T>] The [T] object wrapped in CompletableFuture if found else null
	 */
	override fun search(id: String): CompletableFuture<T?> {
		return CompletableFuture.supplyAsync {
			val data = connection.useResourceWithReturn {
				hget(this@AsyncRedisRepository.id, id)
			}

			return@supplyAsync if (data == null) null else serializer.deserialize(data, controller.classType)
		}
	}

	/**
	 * @param id The ID of the [T] object to delete.
	 */
	override fun delete(id: String) {
		CompletableFuture.runAsync {
			connection.useResource {
				hdel(this@AsyncRedisRepository.id, id)
			}
		}
	}

	/**
	 * @param keys A vararg of keys/ids that will be deleted.
	 */
	override fun deleteMany(vararg keys: String) {
		CompletableFuture.runAsync {
			connection.useResource {
				hdel(this@AsyncRedisRepository.id, *keys)
			}
		}
	}

	/**
	 * @return [List<T>] A list of all the objects in the repository.
	 */
	override fun findAll(): CompletableFuture<List<T>> {
		return CompletableFuture.supplyAsync {
			val data = connection.useResourceWithReturn {
				hgetAll(this@AsyncRedisRepository.id)
			}
			return@supplyAsync data?.values?.map { serializer.deserialize(it, controller.classType)!! }
		}
	}

	/**
	 * @param objects A vararg of [T]'s that need to be saved.
	 *
	 * @return [CompletableFuture<List<T>>] A list of the objects saved.
	 */
	override fun saveMany(vararg objects: T): CompletableFuture<List<T>> {
		return CompletableFuture.supplyAsync {
			connection.useResource {
				hmset(this@AsyncRedisRepository.id, objects.associate { it.identifier to serializer.serialize(it) })
			}

			return@supplyAsync objects.toList()
		}
	}

	/**
	 * @param t The object to save.
	 *
	 * @return [CompletableFuture<T>] The saved object wrapped in a CompletableFuture.
	 */
	override fun save(t: T): CompletableFuture<T> {
		return CompletableFuture.supplyAsync {
			connection.useResource {
				hset(this@AsyncRedisRepository.id, t.identifier, serializer.serialize(t))
			}
			return@supplyAsync t
		}
	}
}
