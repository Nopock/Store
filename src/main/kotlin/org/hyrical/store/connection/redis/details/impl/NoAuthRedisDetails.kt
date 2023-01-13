package org.hyrical.store.connection.redis.details.impl

import org.hyrical.store.connection.redis.details.AbstractRedisDetail
import redis.clients.jedis.JedisPool

class NoAuthRedisDetails(val host: String = "127.0.0.1", val port: Int = 6379) : AbstractRedisDetail() {

    override fun getPool(): JedisPool {
        return JedisPool(host, port)
    }
}