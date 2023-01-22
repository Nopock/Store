package org.hyrical.store.repository.impl.mongodb

import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import org.bson.Document
import org.hyrical.store.DataStoreController
import org.hyrical.store.Storable
import org.hyrical.store.connection.mongo.MongoConnection
import org.hyrical.store.repository.ReactiveRepository
import org.hyrical.store.serializers.Serializers
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.lang.UnsupportedOperationException

class ReactiveMongoRepository<T: Storable>(private val controller: DataStoreController<T>, val connection: MongoConnection) : ReactiveRepository<T> {
    val collection: MongoCollection<Document> = connection.useResourceWithReturn {
        this.getCollection(controller.classType.simpleName)
    } ?: throw UnsupportedOperationException("You did not provide a mongodatabase connection when initiating the owning DataStoreContrller.")

    /**
     * @param id The ID of the [T] object that you are searching for.
     *
     * @return [Mono<T>] The [T] object wrapped in Mono if found else Mono.empty().
     */
    override fun search(id: String): Mono<T> {
        return Mono.justOrEmpty(
            Serializers.activeSerialize.deserialize(collection.find(Filters.eq("_id", id)).first()?.toJson(), controller.classType)
        )
    }

    /**
     * @param id The ID of the [T] object to delete.
     *
     * @return [Mono<Void>] Mono.empty() if the deletion is successful.
     */
    override fun delete(id: String): Mono<Void> {
        return Mono.fromRunnable {
            collection.deleteOne(Filters.eq("_id", id))
        }
    }

    /**
     * @param keys A vararg of keys/ids that will be deleted.
     *
     * @return [Mono<Void>] Mono.empty() if the deletion is successful.
     */
    override fun deleteMany(vararg keys: String): Mono<Void> {
        return Mono.fromRunnable {
            collection.deleteMany(Filters.`in`("_id", keys))
        }
    }

    /**
     * @return [Flux<T>] A flux of all the objects in the repository.
     */
    override fun findAll(): Flux<T> {
        return Flux.fromIterable(
            collection.find().map { Serializers.activeSerialize.deserialize(it.toJson(), controller.classType)!! }
        )
    }

    /**
     * @param objects A vararg of [T]'s that need to be saved.
     *
     * @return [Flux<T>] A flux of the objects saved.
     */
    override fun saveMany(vararg objects: T): Flux<T> {
        return Flux.fromArray(objects)
            .flatMap { save(it) }
    }

    /**
     * @param t The object to save.
     *
     * @return [Mono<T>] The saved object wrapped in Mono.
     */
    override fun save(t: T): Mono<T> {
        return Mono.just(
            t.also {
                collection.updateOne(
                    Filters.eq("_id", t.identifier),
                    Document("\$set", Document.parse(Serializers.activeSerialize.serialize(t))),
                    UpdateOptions().upsert(true)
                )
            }
        )
    }
}