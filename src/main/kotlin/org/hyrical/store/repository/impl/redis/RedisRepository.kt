package org.hyrical.store.repository.impl.redis

import org.hyrical.store.DataStoreController
import org.hyrical.store.Storable
import org.hyrical.store.connection.redis.RedisConnection
import org.hyrical.store.repository.Repository
import org.hyrical.store.serializers.Serializers

class RedisRepository<T : Storable>(private val controller: DataStoreController<T>, val connection: RedisConnection) : Repository<T> {

    private val serializer = Serializers.activeSerializer

    private val id = controller.classType.simpleName

    /**
     * @param [id] The ID of the [T] object that you are searching for.
     *
     * @return [T?] The [T] object if found else null.
     */
    override fun search(id: String): T? {
        return connection.useResourceWithReturn {
            serializer.deserialize(hget(this@RedisRepository.id, id), controller.classType)
        }
    }

    /**
     * @param [id] The ID of the [T] object to delete.
     */
    override fun delete(id: String) {
        return connection.useResource {
            hdel(this@RedisRepository.id, id)
        }
    }

    /**
     * @param [keys] A vararg of keys/ids that will be deleted.
     */
    override fun deleteMany(vararg keys: String) {
        return connection.useResource {
            hdel(this@RedisRepository.id, *keys)
        }
    }

    /**
     * @return [List<T>] A list of all the objects in the repository.
     */
    override fun findAll(): List<T> {
        return connection.useResourceWithReturn {
            hgetAll(this@RedisRepository.id).values.map { serializer.deserialize(it, controller.classType)!! }
        }!!
    }

    /**
     * @param [objects] A vararg of [T]'s that need to be saved.
     *
     * @return [List<T>] A list of the objects saved.
     */
    override fun saveMany(vararg objects: T): List<T> {
        connection.useResource {
            hmset(this@RedisRepository.id, objects.associate { it.identifier to serializer.serialize(it) })
        }
        return objects.toList()
    }

    /**
     * @param [t] The object to save.
     *
     * @return [T] The object saved.
     */
    override fun save(t: T): T {
        connection.useResource {
            hset(this@RedisRepository.id, t.identifier, serializer.serialize(t))
        }
        return t
    }
}