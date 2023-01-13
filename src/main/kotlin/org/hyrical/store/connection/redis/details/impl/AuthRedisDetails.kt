package org.hyrical.store.connection.redis.details.impl

import org.hyrical.store.connection.redis.details.AbstractRedisDetail
import redis.clients.jedis.JedisPool

class AuthRedisDetails(val host: String = "127.0.0.1", val port: Int = 6379, val user: String, val password: String) : AbstractRedisDetail() {
    override fun getPool(): JedisPool {
        return JedisPool(host, port, user, password)
    }
}