package org.hyrical.store.caching

import org.hyrical.store.Storable

abstract class ICachingStrategy<T : Storable> {

    val cache = java.util.HashMap<String, T>()

    init {
        load()
    }

    /**
     * What should happen when the cache is initialized.
     */
    abstract fun load()

    /**
     * @return [Long] The time in milliseconds that the cache should be refreshed (-1 for never)
     */
    abstract fun getExpirationTime(): Long
}