package it.valeriovaudi.vauthenticator.openidconnect.nonce

interface NonceStore {

    fun store(key: String, nonce: String)

    fun load(key: String): String
}