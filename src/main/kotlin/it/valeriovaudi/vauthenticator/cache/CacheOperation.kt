package it.valeriovaudi.vauthenticator.cache

import java.time.Duration
import java.util.Optional

interface CacheOperation<K, O> {
    fun get(key: K): Optional<O>
    fun put(account: O, ttlInSeconds: Duration)
    fun evict(key: K)
}