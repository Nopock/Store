package org.hyrical.store.caching.impl

import org.hyrical.store.DataStoreController
import org.hyrical.store.Storable
import org.hyrical.store.caching.ICachingStrategy

class AllCachingStrategy<T : Storable>(private val controller: DataStoreController<T>) : ICachingStrategy<T>(){

    /**
     * What should happen when the cache is initialized.
     */
    override fun load() {
        controller.repository.findAll().forEach { cache[it.identifier] = it }
    }

    /**
     * @return [Long] The time in milliseconds that the cache should be refreshed (-1 for never)
     */
    override fun getExpirationTime(): Long {
        return -1
    }
}