package org.hyrical.store.connection.redis.details

import redis.clients.jedis.JedisPool

abstract class AbstractRedisDetail {

    abstract fun getPool(): JedisPool
}