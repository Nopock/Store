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

package org.hyrical.store.repository

import org.hyrical.store.DataStoreController
import org.hyrical.store.Storable
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * The base repository for all [Storable] objects,
 * initiated by a [DataStoreController]
 *
 * @author Nopox
 * @since 11/10/22
 */
interface ReactiveRepository<T : Storable> {

	/**
	 * @param id The ID of the [T] object that you are searching for.
	 *
	 * @return [Mono<T>] The [T] object wrapped in Mono if found else Mono.empty().
	 */
	fun search(id: String): Mono<T>

	/**
	 * @param t The object to save.
	 *
	 * @return [Mono<T>] The saved object wrapped in Mono.
	 */
	fun save(t: T): Mono<T>

	/**
	 * @param id The ID of the [T] object to delete.
	 *
	 * @return [Mono<Void>] Mono.empty() if the deletion is successful.
	 */
	fun delete(id: String): Mono<Void>

	/**
	 * @param objects A vararg of [T]'s that need to be saved.
	 *
	 * @return [Flux<T>] A flux of the objects saved.
	 */
	fun saveMany(vararg objects: T): Flux<T>

	/**
	 * @param keys A vararg of keys/ids that will be deleted.
	 *
	 * @return [Mono<Void>] Mono.empty() if the deletion is successful.
	 */
	fun deleteMany(vararg keys: String): Mono<Void>

	/**
	 * @return [Flux<T>] A flux of all the objects in the repository.
	 */
	fun findAll(): Flux<T>
}
