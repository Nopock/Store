package org.hyrical.store.connection


interface DatabaseConnection<C, R> {

    fun useResource(lambda: R.() -> Unit)
    fun <T> useResourceWithReturn(lambda: R.() -> T): T?

    fun getConnection(): C
    fun setConnection(connection: C)

    fun createNewConnection(): C 
}