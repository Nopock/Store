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

package org.hyrical.store.repository.impl.mongodb

import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import org.bson.Document
import org.hyrical.store.DataStoreController
import org.hyrical.store.Storable
import org.hyrical.store.connection.mongo.MongoConnection
import org.hyrical.store.repository.ReactiveRepository
import org.hyrical.store.serializers.Serializers
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

class ReactiveMongoRepository<T : Storable>(
	private val controller: DataStoreController<T>,
	val connection: MongoConnection
) : ReactiveRepository<T> {
	val collection: MongoCollection<Document> = connection.useResourceWithReturn {
		this.getCollection(controller.classType.simpleName)
	}
		?: throw UnsupportedOperationException("You did not provide a mongodatabase connection when initiating the owning DataStoreContrller.")

	/**
	 * @param id The ID of the [T] object that you are searching for.
	 *
	 * @return [Mono<T>] The [T] object wrapped in Mono if found else Mono.empty().
	 */
	override fun search(id: String): Mono<T> {
		return Mono.justOrEmpty(
			Serializers.activeSerializer.deserialize(
				collection.find(Filters.eq("_id", id)).first()?.toJson(),
				controller.classType
			)
		)
	}

	/**
	 * @param id The ID of the [T] object to delete.
	 *
	 * @return [Mono<Void>] Mono.empty() if the deletion is successful.
	 */
	override fun delete(id: String): Mono<Void> {
		return Mono.fromRunnable {
			collection.deleteOne(Filters.eq("_id", id))
		}
	}

	/**
	 * @param keys A vararg of keys/ids that will be deleted.
	 *
	 * @return [Mono<Void>] Mono.empty() if the deletion is successful.
	 */
	override fun deleteMany(vararg keys: String): Mono<Void> {
		return Mono.fromRunnable {
			collection.deleteMany(Filters.`in`("_id", keys))
		}
	}

	/**
	 * @return [Flux<T>] A flux of all the objects in the repository.
	 */
	override fun findAll(): Flux<T> {
		return Flux.fromIterable(
			collection.find().map { Serializers.activeSerializer.deserialize(it.toJson(), controller.classType)!! }
		)
	}

	/**
	 * @param objects A vararg of [T]'s that need to be saved.
	 *
	 * @return [Flux<T>] A flux of the objects saved.
	 */
	override fun saveMany(vararg objects: T): Flux<T> {
		return Flux.fromArray(objects)
			.flatMap { save(it) }
	}

	/**
	 * @param t The object to save.
	 *
	 * @return [Mono<T>] The saved object wrapped in Mono.
	 */
	override fun save(t: T): Mono<T> {
		return Mono.just(
			t.also {
				collection.updateOne(
					Filters.eq("_id", t.identifier),
					Document("\$set", Document.parse(Serializers.activeSerializer.serialize(t))),
					UpdateOptions().upsert(true)
				)
			}
		)
	}
}
