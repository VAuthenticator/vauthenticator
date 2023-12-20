package com.vauthenticator.server.cache

import java.util.*

//todo consider to switch to a spring cache abstraction
interface CacheOperation<K, O> {
    fun get(key: K): Optional<O>
    fun put(key: K, value: O)
    fun evict(key: K)
}