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

package org.hyrical.store.repository.impl.mongodb

import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import org.bson.Document
import org.hyrical.store.DataStoreController
import org.hyrical.store.Storable
import org.hyrical.store.connection.mongo.MongoConnection
import org.hyrical.store.repository.Repository
import org.hyrical.store.serializers.Serializers

class MongoRepository<T : Storable>(private val controller: DataStoreController<T>, val connection: MongoConnection) :
	Repository<T> {

	val collection: MongoCollection<Document> = connection.useResourceWithReturn {
		this.getCollection(controller.classType.simpleName)
	}
		?: throw UnsupportedOperationException("You did not provide a mongodatabase connection when initiating the owning DataStoreContrller.")

	/**
	 * @param [id] The ID of the [T] object that you are searching for.
	 *
	 * @return [T?] The [T] object if found else null.
	 */
	override fun search(id: String): T? {
		return Serializers.activeSerializer.deserialize(
			collection.find(Filters.eq("_id", id)).first()?.toJson(),
			controller.classType
		)
	}

	/**
	 * @param [id] The ID of the [T] object to delete.
	 */
	override fun delete(id: String) {
		collection.deleteOne(Filters.eq("_id", id))
	}

	/**
	 * @param [keys] A vararg of keys/ids that will be deleted.
	 */
	override fun deleteMany(vararg keys: String) {
		collection.deleteMany(Filters.`in`("_id", keys))
	}

	/**
	 * @return [List<T>] A list of all the objects in the repository.
	 */
	override fun findAll(): List<T> {
		return collection.find().map { Serializers.activeSerializer.deserialize(it.toJson(), controller.classType)!! }
			.toList()
	}

	/**
	 * @return [List<T>] A list of all the objects in the repository.
	 */
	override fun saveMany(vararg objects: T): List<T> {
		objects.forEach {
			save(it)
		}
		return objects.toList()
	}

	/**
	 * @param [t] The object to save.
	 *
	 * @return [T] The object saved.
	 */
	override fun save(t: T): T {
		collection.updateOne(
			Filters.eq("_id", t.identifier),
			Document("\$set", Document.parse(Serializers.activeSerializer.serialize(t))),
			UpdateOptions().upsert(true)
		)
		return t
	}
}
