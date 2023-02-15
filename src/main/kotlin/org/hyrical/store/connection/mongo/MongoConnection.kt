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

package org.hyrical.store.connection.mongo

import com.mongodb.MongoClient
import com.mongodb.MongoClientURI
import com.mongodb.client.MongoDatabase
import org.hyrical.store.connection.DatabaseConnection
import org.hyrical.store.connection.mongo.details.AbstractMongoDetail

class MongoConnection(val details: AbstractMongoDetail, val database: String) :
	DatabaseConnection<MongoClient, MongoDatabase> {

	lateinit var handle: MongoClient

	override fun useResource(lambda: MongoDatabase.() -> Unit) {
		val applied = getAppliedResource()
		lambda.invoke(applied)
	}

	override fun <T> useResourceWithReturn(lambda: MongoDatabase.() -> T): T {
		return lambda.invoke(
			getAppliedResource()
		)
	}

	override fun setConnection(connection: MongoClient) {
		handle = connection
	}

	private fun getAppliedResource(): MongoDatabase {
		return try {
			getConnection().getDatabase(database)
		} catch (ignored: Exception) {
			setConnection(createNewConnection())

			getConnection().getDatabase(database)
		}
	}

	override fun getConnection(): MongoClient {
		return try {
			handle
		} catch (e: Exception) {
			createNewConnection()
		}
	}

	override fun createNewConnection(): MongoClient {
		return MongoClient(
			MongoClientURI(details.getURI())
		)
	}
}
