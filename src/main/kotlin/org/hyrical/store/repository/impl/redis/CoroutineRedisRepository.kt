package org.hyrical.store.repository.impl.redis

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.hyrical.store.DataStoreController
import org.hyrical.store.Storable
import org.hyrical.store.connection.redis.RedisConnection
import org.hyrical.store.repository.CoroutineRepository
import org.hyrical.store.serializers.Serializers

class CoroutineRedisRepository<T : Storable>(private val controller: DataStoreController<T>, val connection: RedisConnection) : CoroutineRepository<T> {

    private val serializer = Serializers.activeSerializer

    private val id = controller.classType.simpleName

    /**
     * @param [id] The ID of the [T] object that you are searching for.
     *
     * @return [T?] The [T] object if found else null.
     */
    override suspend fun search(id: String): T? {
        return withContext(Dispatchers.IO){
            connection.useResourceWithReturn {
                serializer.deserialize(hget(this@CoroutineRedisRepository.id, id), controller.classType)
            }
        }
    }

    /**
     * @param [id] The ID of the [T] object to delete.
     */
    override suspend fun delete(id: String) {
        return withContext(Dispatchers.IO){
            connection.useResource {
                hdel(this@CoroutineRedisRepository.id, id)
            }
        }
    }

    /**
     * @param [keys] A vararg of keys/ids that will be deleted.
     */
    override suspend fun deleteMany(vararg keys: String) {
        return withContext(Dispatchers.IO) {
            connection.useResource {
                hdel(this@CoroutineRedisRepository.id, *keys)
            }
        }
    }

    /**
     * @return [List<T>] A list of all the objects in the repository.
     */
    override suspend fun findAll(): List<T> {
        return withContext(Dispatchers.IO) {
            connection.useResourceWithReturn {
                hgetAll(this@CoroutineRedisRepository.id).values.map { serializer.deserialize(it, controller.classType)!! }
            }!!
        }
    }

    /**
     * @param [objects] A vararg of [T]'s that need to be saved.
     *
     * @return [List<T>] A list of the objects saved.
     */
    override suspend fun saveMany(vararg objects: T): List<T> {
        return withContext(Dispatchers.IO) {
            connection.useResource {
                hmset(this@CoroutineRedisRepository.id, objects.associate { it.identifier to serializer.serialize(it) })
            }
            objects.toList()
        }
    }

    /**
     * @param [t] The object to save.
     *
     * @return [T] The object saved.
     */
    override suspend fun save(t: T): T {
        return withContext(Dispatchers.IO) {
            connection.useResource {
                hset(this@CoroutineRedisRepository.id, t.identifier, serializer.serialize(t))
            }
            t
        }
    }
}