package org.hyrical.store.connection.redis

import org.hyrical.store.connection.DatabaseConnection
import org.hyrical.store.connection.redis.details.AbstractRedisDetail
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import redis.clients.jedis.exceptions.JedisException

class RedisConnection(val details: AbstractRedisDetail) : DatabaseConnection<JedisPool, Jedis> {

    lateinit var handle: JedisPool

    override fun <T> useResourceWithReturn(lambda: Jedis.() -> T): T? {
        return try {
            val applied = getAppliedResource()

            val resource = lambda.invoke(applied)
            applied.close()

            resource
        } catch (exception: JedisException) {
            null
        }
    }

    override fun setConnection(connection: JedisPool) {
        handle = connection
    }

    fun getAppliedResource(): Jedis {
        return try {
            handle.resource
        } catch (exception: Exception) {
            val connection = createNewConnection()
            setConnection(connection)

            connection.resource
        }
    }

    override fun useResource(lambda: Jedis.() -> Unit) {
        val applied = getAppliedResource()
        lambda.invoke(applied)

        applied.close()
    }

    override fun getConnection(): JedisPool {
        return handle
    }

    override fun createNewConnection(): JedisPool
    {
        return details.getPool()
    }
}