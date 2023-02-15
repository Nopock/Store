/*
 * MIT License
 *
 * Copyright (c) 2023 Nathan Weisz
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
import org.hyrical.store.repository.Repository
import org.hyrical.store.serializers.Serializers

class FlatFileRepository<T : Storable>(controller: DataStoreController<T>, val connection: FlatFileConnection) :
	Repository<T> {

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

	override fun search(id: String): T? {
		return cache[id]
	}

	override fun save(t: T): T {
		cache[t.identifier] = t
		persistToFile()
		return t
	}

	override fun delete(id: String) {
		cache.remove(id)
		persistToFile()
	}

	override fun saveMany(vararg objects: T): List<T> {
		objects.forEach { obj -> cache[obj.identifier] = obj }
		persistToFile()
		return objects.toList()
	}

	override fun deleteMany(vararg keys: String) {
		keys.forEach { key -> cache.remove(key) }
		persistToFile()
	}

	override fun findAll(): List<T> {
		return cache.values.toList()
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
