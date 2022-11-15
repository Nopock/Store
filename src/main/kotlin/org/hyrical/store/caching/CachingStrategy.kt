package org.hyrical.store.caching

import org.hyrical.store.Storable
import org.hyrical.store.caching.impl.NoneCachingStrategy

enum class CachingStrategy {
    NONE() {
        override fun <T : Storable> constructCache(): ICachingStrategy<T> {
            return NoneCachingStrategy()
        }
    },
    ALL() {
        override fun <T : Storable> constructCache(): ICachingStrategy<T> {
            return NoneCachingStrategy()
        }
    };

    abstract fun <T : Storable> constructCache(): ICachingStrategy<T>
}