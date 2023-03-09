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

package org.hyrical.store.connection.flatfile

import org.hyrical.store.connection.DatabaseConnection
import java.io.File

class FlatFileConnection(val directory: String, val fileName: String) : DatabaseConnection<File, File> {

	lateinit var handle: File

	override fun useResource(lambda: File.() -> Unit) {
		val applied = getAppliedResource()
		lambda.invoke(applied)
	}

	override fun <T> useResourceWithReturn(lambda: File.() -> T): T? {
		return lambda.invoke(
			getAppliedResource()
		)
	}

	override fun getConnection(): File {
		return try {
			handle
		} catch (e: Exception) {
			createNewConnection()
		}
	}

	override fun createNewConnection(): File {
		return File("$directory$fileName.json").apply {
			if (!exists()) {
				createNewFile()
			}
		}
	}

	override fun setConnection(connection: File) {
		handle = connection
	}

	private fun getAppliedResource(): File {
		return try {
			getConnection()
		} catch (ignored: Exception) {
			setConnection(createNewConnection())

			getConnection()
		}
	}

}
