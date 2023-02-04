package org.hyrical.store

/**
 * The base interface that all classes stored by [DataStoreController]'s
 * must implement.
 *
 * @author Nopox
 * @since 11/10/22
 */
interface Storable {
    val identifier: String
}