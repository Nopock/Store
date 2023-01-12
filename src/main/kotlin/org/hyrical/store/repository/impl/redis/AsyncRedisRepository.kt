package org.hyrical.store.repository.impl.redis

import org.hyrical.store.DataStoreController
import org.hyrical.store.Storable
import org.hyrical.store.constants.DataTypeResources
import org.hyrical.store.repository.AsyncRepository
import org.hyrical.store.serializers.Serializers
import redis.clients.jedis.Jedis
import java.lang.UnsupportedOperationException
import java.util.concurrent.CompletableFuture

class AsyncRedisRepository<T : Storable>(private val controller: DataStoreController<T>) : AsyncRepository<T> {

    private val jedis: Jedis = DataTypeResources.redisClient ?: throw UnsupportedOperationException("There was an error whilst enabling a redis repository. To fix this call DataTypeResources#enableRedisRepositories.")

    private val serializer = Serializers.activeSerialize

    private val id = controller.classType.simpleName

    /**
     * @param id The ID of the [T] object that you are searching for.
     *
     * @return [CompletableFuture<T>] The [T] object wrapped in CompletableFuture if found else null
     */
    override fun search(id: String): CompletableFuture<T?> {
        return CompletableFuture.supplyAsync {
            val data = jedis.hget(this.id, id)
            return@supplyAsync if (data == null) null else serializer.deserialize(data, controller.classType)
        }
    }

    /**
     * @param id The ID of the [T] object to delete.
     */
    override fun delete(id: String) {
        CompletableFuture.runAsync {
            jedis.hdel(this.id, id)
        }
    }

    /**
     * @param keys A vararg of keys/ids that will be deleted.
     */
    override fun deleteMany(vararg keys: String) {
        CompletableFuture.runAsync {
            jedis.hdel(this.id, *keys)
        }
    }

    /**
     * @return [List<T>] A list of all the objects in the repository.
     */
    override fun findAll(): CompletableFuture<List<T>> {
        return CompletableFuture.supplyAsync {
            val data = jedis.hgetAll(this.id)
            return@supplyAsync data.values.map { serializer.deserialize(it, controller.classType)!! }
        }
    }

    /**
     * @param objects A vararg of [T]'s that need to be saved.
     *
     * @return [CompletableFuture<List<T>>] A list of the objects saved.
     */
    override fun saveMany(vararg objects: T): CompletableFuture<List<T>> {
        return CompletableFuture.supplyAsync {
            return@supplyAsync objects.toList().also {
                jedis.hmset(this.id, objects.associate { it.identifier to serializer.serialize(it) })
            }
        }
    }

    /**
     * @param t The object to save.
     *
     * @return [CompletableFuture<T>] The saved object wrapped in a CompletableFuture.
     */
    override fun save(t: T): CompletableFuture<T> {
        return CompletableFuture.supplyAsync {
            return@supplyAsync t.also {
                jedis.hset(this.id, t.identifier, serializer.serialize(t))
            }
        }
    }
}