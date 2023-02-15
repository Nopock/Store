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

package org.hyrical.store.tests.flatfile

import org.hyrical.store.DataStoreController
import org.hyrical.store.connection.flatfile.FlatFileConnection
import org.hyrical.store.tests.obj.UserTest
import org.hyrical.store.type.StorageType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class FlatFileTests {

	private val controller by lazy {
		DataStoreController.of<UserTest>(
			StorageType.FLAT_FILE,
			FlatFileConnection(
				FlatFileTests::class.java.protectionDomain.codeSource.location.toURI().path,
				"flatfile_tests"
			)
		)
	}

	@Test
	fun flatfile_retrieve_test() {
		val data = UserTest("retrieve-test-identifier", "Name", 1)

		controller.repository.save(data)

		assertEquals(data, controller.repository.search("retrieve-test-identifier"))
		controller.repository.delete("retrieve-test-identifier")
	}

	@Test
	fun flatfile_retrieve_all_test() {
		val data = UserTest.random(5)

		// Note might need to clear all

		data.forEach {
			controller.repository.save(it)
		}

		assertEquals(data, controller.repository.findAll())

		controller.repository.findAll().forEach {
			controller.repository.delete(it.identifier)
		}
	}
}
