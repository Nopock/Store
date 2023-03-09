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
import org.hyrical.store.repository.Repository
import org.hyrical.store.serializers.Serializers

class RedisRepository<T : Storable>(private val controller: DataStoreController<T>, val connection: RedisConnection) :
	Repository<T> {

	private val serializer = Serializers.activeSerializer

	private val id = controller.classType.simpleName

	/**
	 * @param [id] The ID of the [T] object that you are searching for.
	 *
	 * @return [T?] The [T] object if found else null.
	 */
	override fun search(id: String): T? {
		return connection.useResourceWithReturn {
			serializer.deserialize(hget(this@RedisRepository.id, id), controller.classType)
		}
	}

	/**
	 * @param [id] The ID of the [T] object to delete.
	 */
	override fun delete(id: String) {
		return connection.useResource {
			hdel(this@RedisRepository.id, id)
		}
	}

	/**
	 * @param [keys] A vararg of keys/ids that will be deleted.
	 */
	override fun deleteMany(vararg keys: String) {
		return connection.useResource {
			hdel(this@RedisRepository.id, *keys)
		}
	}

	/**
	 * @return [List<T>] A list of all the objects in the repository.
	 */
	override fun findAll(): List<T> {
		return connection.useResourceWithReturn {
			hgetAll(this@RedisRepository.id).values.map { serializer.deserialize(it, controller.classType)!! }
		}!!
	}

	/**
	 * @param [objects] A vararg of [T]'s that need to be saved.
	 *
	 * @return [List<T>] A list of the objects saved.
	 */
	override fun saveMany(vararg objects: T): List<T> {
		connection.useResource {
			hmset(this@RedisRepository.id, objects.associate { it.identifier to serializer.serialize(it) })
		}
		return objects.toList()
	}

	/**
	 * @param [t] The object to save.
	 *
	 * @return [T] The object saved.
	 */
	override fun save(t: T): T {
		connection.useResource {
			hset(this@RedisRepository.id, t.identifier, serializer.serialize(t))
		}
		return t
	}
}
