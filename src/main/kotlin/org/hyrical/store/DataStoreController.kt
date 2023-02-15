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

package org.hyrical.store

import com.google.common.collect.HashBasedTable
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
class DataStoreController<T : Storable>(
	private val type: StorageType,
	val classType: Class<T>,
	val connection: DatabaseConnection<*, *>?
) {

	init {
		existingControllers.put(classType, type, this)
	}

	companion object {

		/**
		 * A map of all the [DataStoreController]'s that have been created.
		 */
		private val existingControllers: HashBasedTable<Class<*>, StorageType, DataStoreController<*>> =
			HashBasedTable.create()

		/**
		 * @param [classType] The type of [Storable] to be stored.
		 * @param [type] The type of [StorageType] to be used.
		 *
		 * @return [DataStoreController<T>] The [DataStoreController] for the given [T] and [type].
		 */
		@JvmStatic
		fun existing(type: StorageType, classType: Class<Storable>): DataStoreController<*>? {
			return existingControllers.get(classType, type)
		}

		/**
		 * Creates a new instance of the [DataStoreController] with
		 * the specified [StorageType]
		 *
		 * @param [type] An objects that implements [Storable] (The type of data to be stored)
		 *
		 * @see [DataStoreController]
		 */
		inline fun <reified T : Storable> of(
			type: StorageType,
			connection: DatabaseConnection<*, *>? = null
		): DataStoreController<T> {
			return DataStoreController(type, T::class.java, connection)
		}

		@JvmStatic
		fun <T : Storable> of(
			type: StorageType,
			t: Class<T>,
			connection: DatabaseConnection<*, *>? = null
		): DataStoreController<T> {
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
	fun bindFlatFileDirectory(directory: String) {
		if (type == StorageType.FLAT_FILE) {
			this.directory = directory
		} else {
			throw UnsupportedOperationException("You attempted to bind a flat file directory to a non flat file DataStoreController!!")
		}
	}
}
