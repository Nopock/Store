package org.hyrical.store.repository.impl.flatfile

import org.hyrical.store.DataStoreController
import org.hyrical.store.Storable
import org.hyrical.store.repository.Repository
import org.hyrical.store.serializers.Serializers
import java.io.File
import java.io.FileReader
import java.util.ArrayList

class FlatFileRepository<T : Storable>(controller: DataStoreController<T>) : Repository<T> {

    val file: File = File(controller.directory, controller.classType.simpleName + ".json").also {
        if (!it.exists()) it.createNewFile()
    }

    val cache: MutableList<T> = mutableListOf()

    init {
        FileReader(file).use {
            val jsonString = it.readText()
            val jsonArray = Serializers.activeSerialize.deserialize(jsonString, ArrayList<T>().javaClass)
            cache.addAll(jsonArray!!)
        }
    }

    /**
     * @param [id] The ID of the [T] object that you are searching for.
     *
     * @return [T?] The [T] object if found else null.
     */
    override fun search(id: String): T? {
        return cache.firstOrNull { it.identifier == id }
    }

    /**
     * @param [id] The ID of the [T] object to delete.
     */
    override fun delete(id: String) {
        cache.removeIf { it.identifier == id }
        file.writeText(Serializers.activeSerialize.serialize(cache)!!)
    }

    /**
     * @param [keys] A vararg of keys/ids that will be deleted.
     */
    override fun deleteMany(vararg keys: String) {
        cache.removeIf { keys.contains(it.identifier) }
        file.writeText(Serializers.activeSerialize.serialize(cache)!!)
    }

    /**
     * @return [List<T>] A list of all the objects in the repository.
     */
    override fun findAll(): List<T> {
        return cache
    }

    /**
     * @param [objects] A vararg of [T]'s that need to be saved.
     *
     * @return [List<T>] A list of the objects saved.
     */
    override fun saveMany(vararg objects: T): List<T> {
        cache.addAll(objects)
        file.writeText(Serializers.activeSerialize.serialize(cache)!!)
        return cache
    }

    /**
     * @param [t] The object to save.
     *
     * @return [T] The object saved.
     */
    override fun save(t: T): T {
        cache.add(t)
        file.writeText(Serializers.activeSerialize.serialize(cache)!!)
        return t
    }
}