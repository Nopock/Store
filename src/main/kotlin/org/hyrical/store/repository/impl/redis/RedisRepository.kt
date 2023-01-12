package org.hyrical.store.repository.impl.redis

import org.hyrical.store.DataStoreController
import org.hyrical.store.Storable
import org.hyrical.store.constants.DataTypeResources
import org.hyrical.store.repository.Repository
import org.hyrical.store.serializers.Serializers
import redis.clients.jedis.Jedis
import java.lang.UnsupportedOperationException

class RedisRepository<T : Storable>(private val controller: DataStoreController<T>) : Repository<T> {

    private val jedis: Jedis = DataTypeResources.redisClient ?: throw UnsupportedOperationException("There was an error whilst enabling a redis repository. To fix this call DataTypeResources#enableRedisRepositories.")

    private val serializer = Serializers.activeSerialize

    private val id = controller.classType.simpleName

    /**
     * @param [id] The ID of the [T] object that you are searching for.
     *
     * @return [T?] The [T] object if found else null.
     */
    override fun search(id: String): T? {
        return serializer.deserialize(jedis.hget(this.id, id), controller.classType)
    }

    /**
     * @param [id] The ID of the [T] object to delete.
     */
    override fun delete(id: String) {
        jedis.hdel(this.id, id)
    }

    /**
     * @param [keys] A vararg of keys/ids that will be deleted.
     */
    override fun deleteMany(vararg keys: String) {
        jedis.hdel(this.id, *keys)
    }

    /**
     * @return [List<T>] A list of all the objects in the repository.
     */
    override fun findAll(): List<T> {
        return jedis.hgetAll(this.id).values.map { serializer.deserialize(it, controller.classType)!! }
    }

    /**
     * @param [objects] A vararg of [T]'s that need to be saved.
     *
     * @return [List<T>] A list of the objects saved.
     */
    override fun saveMany(vararg objects: T): List<T> {
        return objects.toList().also {
            jedis.hmset(this.id, objects.associate { it.identifier to serializer.serialize(it) })
        }
    }

    /**
     * @param [t] The object to save.
     *
     * @return [T] The object saved.
     */
    override fun save(t: T): T {
        return t.also {
            jedis.hset(this.id, t.identifier, serializer.serialize(t))
        }
    }
}