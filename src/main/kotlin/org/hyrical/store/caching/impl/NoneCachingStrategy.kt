package org.hyrical.store.caching.impl

import org.hyrical.store.Storable
import org.hyrical.store.caching.ICachingStrategy

class NoneCachingStrategy<T : Storable> : ICachingStrategy<T>() {

    /**
     * What should happen when the cache is initialized.
     */
    override fun load() {
        // Do nothing.
    }

    /**
     * @return [Long] The time in milliseconds that the cache should be refreshed (-1 for never)
     */
    override fun getExpirationTime(): Long {
        return -1
    }
}