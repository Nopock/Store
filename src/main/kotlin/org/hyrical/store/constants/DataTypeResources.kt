package org.hyrical.store.constants

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.bson.Document
import redis.clients.jedis.Jedis


object DataTypeResources  {

    var redisClient: Jedis? = null
    var mongoClient: MongoClient? = null
    var mongoDatabase: MongoDatabase? = null

    val mongoCollections: MutableMap<String, MongoCollection<Document>> = mutableMapOf()

    @JvmStatic
    fun enableRedisRepositories(uri: String) {
        redisClient = Jedis(uri)
    }

    @JvmStatic
    fun enableRedisRepositories(host: String = "127.0.0.1", port: Int = 6379) {
        redisClient = Jedis(host, port)
    }

    @JvmStatic
    fun enableMongoRepositories(uri: String, database: String) {
        mongoClient = MongoClients.create(uri)
        mongoDatabase = mongoClient!!.getDatabase(database)
    }

    @JvmStatic
    fun enableMongoRepositories(host: String = "127.0.0.1", port: Int = 27017, database: String) {
        mongoClient = MongoClients.create("mongodb://$host:$port")
        mongoDatabase = mongoClient!!.getDatabase(database)
    }
}