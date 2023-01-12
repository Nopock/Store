package org.hyrical.store.repository.impl.redis

import org.hyrical.store.DataStoreController
import org.hyrical.store.Storable
import org.hyrical.store.constants.DataTypeResources
import org.hyrical.store.repository.Repository
import org.hyrical.store.serializers.Serializers
import redis.clients.jedis.Jedis
import java.lang.UnsupportedOperationException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import org.hyrical.store.repository.ReactiveRepository

class ReactiveRedisRepository<T: Storable>(private val controller: DataStoreController<T>) : ReactiveRepository<T> {

    private val jedis: Jedis = DataTypeResources.redisClient ?: throw UnsupportedOperationException("There was an error whilst enabling a redis repository. To fix this call DataTypeResources#enableRedisRepositories.")

    private val serializer = Serializers.activeSerialize

    private val id = controller.classType.simpleName

    /**
     * @param id The ID of the [T] object that you are searching for.
     *
     * @return [Mono<T>] The [T] object wrapped in Mono if found else Mono.empty().
     */
    override fun search(id: String): Mono<T> {
        return Mono.justOrEmpty(
            serializer.deserialize(jedis.hget(this.id, id), controller.classType)
        )
    }

    /**
     * @param id The ID of the [T] object to delete.
     *
     * @return [Mono<Void>] Mono.empty() if the deletion is successful.
     */
    override fun delete(id: String): Mono<Void> {
        return Mono.fromRunnable {
            jedis.hdel(this.id, id)
        }
    }

    /**
     * @param keys A vararg of keys/ids that will be deleted.
     *
     * @return [Mono<Void>] Mono.empty() if the deletion is successful.
     */
    override fun deleteMany(vararg keys: String): Mono<Void> {
        return Mono.fromRunnable {
            jedis.hdel(this.id, *keys)
        }
    }

    /**
     * @return [Flux<T>] A flux of all the objects in the repository.
     */
    override fun findAll(): Flux<T> {
        return Flux.fromIterable(
            jedis.hgetAll(this.id).values.map { serializer.deserialize(it, controller.classType)!! }
        )
    }

    /**
     * @param objects A vararg of [T]'s that need to be saved.
     *
     * @return [Flux<T>] A flux of the objects saved.
     */
    override fun saveMany(vararg objects: T): Flux<T> {
        return Flux.fromArray(objects.also {
            jedis.hmset(this.id, objects.associate { it.identifier to serializer.serialize(it) })
        })
    }

    /**
     * @param t The object to save.
     *
     * @return [Mono<T>] The saved object wrapped in Mono.
     */
    override fun save(t: T): Mono<T> {
        return Mono.just(
            t.also {
                jedis.hset(this.id, t.identifier, serializer.serialize(t))
            }
        )
    }
}