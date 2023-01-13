package org.hyrical.store.repository.impl.flatfile

import org.hyrical.store.DataStoreController
import org.hyrical.store.Storable
import org.hyrical.store.repository.ReactiveRepository
import org.hyrical.store.serializers.Serializers
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.File

class ReactiveFlatFileRepository<T: Storable>(private val controller: DataStoreController<T>) : ReactiveRepository<T> {

    val file: File = File(controller.directory, controller.classType.simpleName + ".json").also {
        if (!it.exists()) it.createNewFile()
    }

    val cache: MutableList<T> = mutableListOf()

    /**
     * @param id The ID of the [T] object that you are searching for.
     *
     * @return [Mono<T>] The [T] object wrapped in Mono if found else Mono.empty().
     */
    override fun search(id: String): Mono<T> {
        return Mono.justOrEmpty(
            cache.firstOrNull { it.identifier == id }
        )
    }

    /**
     * @param id The ID of the [T] object to delete.
     *
     * @return [Mono<Void>] Mono.empty() if the deletion is successful.
     */
    override fun delete(id: String): Mono<Void> {
        return Mono.fromRunnable {
            cache.removeIf { it.identifier == id }
            file.writeText(Serializers.activeSerialize.serialize(cache)!!)
        }
    }

    /**
     * @param keys A vararg of keys/ids that will be deleted.
     *
     * @return [Mono<Void>] Mono.empty() if the deletion is successful.
     */
    override fun deleteMany(vararg keys: String): Mono<Void> {
        return Mono.fromRunnable {
            cache.removeIf { keys.contains(it.identifier) }
            file.writeText(Serializers.activeSerialize.serialize(cache)!!)
        }
    }

    /**
     * @return [Flux<T>] A flux of all the objects in the repository.
     */
    override fun findAll(): Flux<T> {
        return Flux.fromIterable(
            cache
        )
    }

    /**
     * @param objects A vararg of [T]'s that need to be saved.
     *
     * @return [Flux<T>] A flux of the objects saved.
     */
    override fun saveMany(vararg objects: T): Flux<T> {
        return Flux.fromArray(objects).also {
            cache.addAll(objects)
            file.writeText(Serializers.activeSerialize.serialize(cache)!!)
        }

    }

    /**
     * @param t The object to save.
     *
     * @return [Mono<T>] The saved object wrapped in Mono.
     */
    override fun save(t: T): Mono<T> {
        return Mono.just(
            t.also {
                cache.add(t)
                file.writeText(Serializers.activeSerialize.serialize(cache)!!)
            }
        )
    }
}