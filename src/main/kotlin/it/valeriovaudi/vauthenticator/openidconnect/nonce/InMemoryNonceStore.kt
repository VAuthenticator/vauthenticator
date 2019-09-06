package it.valeriovaudi.vauthenticator.openidconnect.nonce

import java.util.*
import java.util.concurrent.ConcurrentHashMap

class InMemoryNonceStore(private val store: ConcurrentHashMap<String, String>) : NonceStore {

    override fun load(key: String) =
            Optional.ofNullable(store.remove(key)).orElse("")


    override fun store(key: String, nonce: String) {
        store.put(key, nonce)
    }
}