package org.hyrical.store.repository.impl.flatfile

import org.hyrical.store.DataStoreController
import org.hyrical.store.Storable
import org.hyrical.store.repository.ReactiveRepository
import org.hyrical.store.serializers.Serializers
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.File
import java.io.FileReader
import java.util.ArrayList
import com.google.gson.reflect.TypeToken

class ReactiveFlatFileRepository<T: Storable>(controller: DataStoreController<T>) : ReactiveRepository<T> {
    
    val file: File = File(controller.directory, controller.classType.simpleName + ".json").also {
        if (!it.exists()) it.createNewFile()
    }

    val cache = mutableMapOf<String, T>()
    
    init {
        // Read the file and deserialize the contents into the cache map
        val jsonString = file.readText()
        val type = TypeToken.getParameterized(ArrayList::class.java, controller.classType).type
        val objects = Serializers.activeSerializer.deserialize<ArrayList<T>>(jsonString, type)
        objects?.forEach { obj -> cache[obj.identifier] = obj }   
    }

    /**
     * @param id The ID of the [T] object that you are searching for.
     *
     * @return [Mono<T>] The [T] object wrapped in Mono if found else Mono.empty().
     */
    override fun search(id: String): Mono<T> {
        return Mono.justOrEmpty(
            cache[id]
        )
    }

    /**
     * @param id The ID of the [T] object to delete.
     *
     * @return [Mono<Void>] Mono.empty() if the deletion is successful.
     */
    override fun delete(id: String): Mono<Void> {
        return Mono.fromRunnable {       
            cache.remove(id)
            persistToFile()
        }
    }

    /**
     * @param keys A vararg of keys/ids that will be deleted.
     *
     * @return [Mono<Void>] Mono.empty() if the deletion is successful.
     */
    override fun deleteMany(vararg keys: String): Mono<Void> {
        return Mono.fromRunnable {
            keys.forEach { key -> cache.remove(key) }
            persistToFile()
        }
    }

    /**
     * @return [Flux<T>] A flux of all the objects in the repository.
     */
    override fun findAll(): Flux<T> {
        return Flux.fromIterable(
            cache.values.toList()
        )
    }

    /**
     * @param objects A vararg of [T]'s that need to be saved.
     *
     * @return [Flux<T>] A flux of the objects saved.
     */
    override fun saveMany(vararg objects: T): Flux<T> {
        return Flux.fromArray(objects).also {
            objects.forEach { obj -> cache[obj.identifier] = obj }
            persistToFile()
            objects.toList()
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
                cache[t.identifier] = t
                persistToFile()
            }
        )
    }

    private fun persistToFile() {
        // Serialize the cache map and write it to the file
        val jsonString = Serializers.activeSerializer.serialize(cache.values)
        file.writeText(jsonString!!)
    }
}