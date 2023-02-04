package org.hyrical.store.tests.obj

import org.hyrical.store.Storable
import java.util.*

data class UserTest(
    override val identifier: String,
    val name: String,
    val age: Int
) : Storable {
    companion object {
        fun random(amount: Int): List<UserTest> {
            val users = mutableListOf<UserTest>()

            for (i in 0..amount) {
                users.add(
                    UserTest(
                        UUID.randomUUID().toString(),
                        "Test User Name | $i",
                        i
                    )
                )
            }

            return users
        }
    }
}