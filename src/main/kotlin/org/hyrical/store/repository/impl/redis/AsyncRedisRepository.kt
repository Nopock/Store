package org.hyrical.store.repository.impl.redis

import org.hyrical.store.DataStoreController
import org.hyrical.store.Storable
import org.hyrical.store.connection.redis.RedisConnection
import org.hyrical.store.repository.AsyncRepository
import org.hyrical.store.serializers.Serializers
import redis.clients.jedis.Jedis
import java.lang.UnsupportedOperationException
import java.util.concurrent.CompletableFuture

class AsyncRedisRepository<T : Storable>(private val controller: DataStoreController<T>, val connection: RedisConnection) : AsyncRepository<T> {

    private val serializer = Serializers.activeSerialize

    private val id = controller.classType.simpleName

    /**
     * @param id The ID of the [T] object that you are searching for.
     *
     * @return [CompletableFuture<T>] The [T] object wrapped in CompletableFuture if found else null
     */
    override fun search(id: String): CompletableFuture<T?> {
        return CompletableFuture.supplyAsync {
            val data = connection.useResourceWithReturn {
                hget(this@AsyncRedisRepository.id, id)
            }

            return@supplyAsync if (data == null) null else serializer.deserialize(data, controller.classType)
        }
    }

    /**
     * @param id The ID of the [T] object to delete.
     */
    override fun delete(id: String) {
        CompletableFuture.runAsync {
            connection.useResource {
                hdel(this@AsyncRedisRepository.id, id)
            }
        }
    }

    /**
     * @param keys A vararg of keys/ids that will be deleted.
     */
    override fun deleteMany(vararg keys: String) {
        CompletableFuture.runAsync {
            connection.useResource {
                hdel(this@AsyncRedisRepository.id, *keys)
            }
        }
    }

    /**
     * @return [List<T>] A list of all the objects in the repository.
     */
    override fun findAll(): CompletableFuture<List<T>> {
        return CompletableFuture.supplyAsync {
            val data = connection.useResourceWithReturn {
                hgetAll(this@AsyncRedisRepository.id)
            }
            return@supplyAsync data?.values?.map { serializer.deserialize(it, controller.classType)!! }
        }
    }

    /**
     * @param objects A vararg of [T]'s that need to be saved.
     *
     * @return [CompletableFuture<List<T>>] A list of the objects saved.
     */
    override fun saveMany(vararg objects: T): CompletableFuture<List<T>> {
        return CompletableFuture.supplyAsync {
            connection.useResource {
                hmset(this@AsyncRedisRepository.id, objects.associate { it.identifier to serializer.serialize(it) })
            }

            return@supplyAsync objects.toList()
        }
    }

    /**
     * @param t The object to save.
     *
     * @return [CompletableFuture<T>] The saved object wrapped in a CompletableFuture.
     */
    override fun save(t: T): CompletableFuture<T> {
        return CompletableFuture.supplyAsync {
            connection.useResource {
                hset(this@AsyncRedisRepository.id, t.identifier, serializer.serialize(t))
            }
            return@supplyAsync t
        }
    }
}