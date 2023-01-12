package org.hyrical.store.connection

import java.io.Closable

interface DatabaseConnection<C, R> : Closable {

    fun useResource(lambda: R.() -> Unit)
    fun <T> useResourceWithReturn(lambda: R.() -> T): T?

    fun getConnection(): C
    fun setConnection(connection: C)

    fun createNewConnection(): C 
}