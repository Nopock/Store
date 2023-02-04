package org.hyrical.store.tests.flatfile

import org.hyrical.store.DataStoreController
import org.hyrical.store.connection.flatfile.FlatFileConnection
import org.hyrical.store.tests.obj.UserTest
import org.hyrical.store.type.StorageType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class FlatFileTests {

    private val controller by lazy {
        DataStoreController.of<UserTest>(StorageType.FLAT_FILE, FlatFileConnection(FlatFileTests::class.java.protectionDomain.codeSource.location.toURI().path, "flatfile_tests"))
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