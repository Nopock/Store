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

package org.hyrical.store.repository.impl.flatfile

import com.google.gson.reflect.TypeToken
import org.hyrical.store.DataStoreController
import org.hyrical.store.Storable
import org.hyrical.store.connection.flatfile.FlatFileConnection
import org.hyrical.store.repository.ReactiveRepository
import org.hyrical.store.serializers.Serializers
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.File

class ReactiveFlatFileRepository<T : Storable>(controller: DataStoreController<T>, val connection: FlatFileConnection) :
	ReactiveRepository<T> {

	val file: File = File(connection.directory, controller.classType.simpleName + ".json").also {
		if (!it.exists()) it.createNewFile()
	}

	val cache = mutableMapOf<String, T>()

	init {
		// Read the file and deserialize the contents into the cache map
		val jsonString = connection.useResourceWithReturn {
			readText()
		}
		val type = TypeToken.getParameterized(ArrayList::class.java, controller.classType).type
		val objects = Serializers.activeSerializer.deserialize<ArrayList<T>>(jsonString, type)
		objects?.forEach { obj -> cache[obj.identifier] = obj }
	}

	/**
	 * @param id The ID of the [T] object that you are searching for.
	 *
	 * @return [Mono<T>] The [T] object wrapped in Mono if found else Mono.empty().
	 */
	override fun search(id: String): Mono<T> {
		return Mono.justOrEmpty(
			cache[id]
		)
	}

	/**
	 * @param id The ID of the [T] object to delete.
	 *
	 * @return [Mono<Void>] Mono.empty() if the deletion is successful.
	 */
	override fun delete(id: String): Mono<Void> {
		return Mono.fromRunnable {
			cache.remove(id)
			persistToFile()
		}
	}

	/**
	 * @param keys A vararg of keys/ids that will be deleted.
	 *
	 * @return [Mono<Void>] Mono.empty() if the deletion is successful.
	 */
	override fun deleteMany(vararg keys: String): Mono<Void> {
		return Mono.fromRunnable {
			keys.forEach { key -> cache.remove(key) }
			persistToFile()
		}
	}

	/**
	 * @return [Flux<T>] A flux of all the objects in the repository.
	 */
	override fun findAll(): Flux<T> {
		return Flux.fromIterable(
			cache.values.toList()
		)
	}

	/**
	 * @param objects A vararg of [T]'s that need to be saved.
	 *
	 * @return [Flux<T>] A flux of the objects saved.
	 */
	override fun saveMany(vararg objects: T): Flux<T> {
		return Flux.fromArray(objects).also {
			objects.forEach { obj -> cache[obj.identifier] = obj }
			persistToFile()
			objects.toList()
		}

	}

	/**
	 * @param t The object to save.
	 *
	 * @return [Mono<T>] The saved object wrapped in Mono.
	 */
	override fun save(t: T): Mono<T> {
		return Mono.just(
			t.also {
				cache[t.identifier] = t
				persistToFile()
			}
		)
	}

	private fun persistToFile() {
		// Serialize the cache map and write it to the file
		val jsonString = Serializers.activeSerializer.serialize(cache.values)

		connection.useResource {
			if (jsonString != null) {
				writeText(jsonString)
			}
		}
	}
}
