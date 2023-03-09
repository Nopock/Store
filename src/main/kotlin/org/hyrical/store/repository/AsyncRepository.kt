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

package org.hyrical.store.repository

import org.hyrical.store.DataStoreController
import org.hyrical.store.Storable
import java.util.concurrent.CompletableFuture

/**
 * The base repository for all [Storable] objects,
 * initiated by a [DataStoreController]
 *
 * @author Nopox
 * @since 1/12/2023
 */
interface AsyncRepository<T : Storable> {

	/**
	 * @param id The ID of the [T] object that you are searching for.
	 *
	 * @return [CompletableFuture<T>] The [T] object wrapped in CompletableFuture if found else null
	 */
	fun search(id: String): CompletableFuture<T?>

	/**
	 * @param t The object to save.
	 *
	 * @return [CompletableFuture<T>] The saved object wrapped in a CompletableFuture.
	 */
	fun save(t: T): CompletableFuture<T>

	/**
	 * @param id The ID of the [T] object to delete.
	 */
	fun delete(id: String)

	/**
	 * @param objects A vararg of [T]'s that need to be saved.
	 *
	 * @return [CompletableFuture<List<T>>] A list of the objects saved.
	 */
	fun saveMany(vararg objects: T): CompletableFuture<List<T>>


	/**
	 * @param keys A vararg of keys/ids that will be deleted.
	 */
	fun deleteMany(vararg keys: String)

	/**
	 * @return [CompletableFuture<List<T>>] A list of all the objects in the repository.
	 */
	fun findAll(): CompletableFuture<List<T>>
}
