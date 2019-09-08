package it.valeriovaudi.vauthenticator.openid.connect.nonce

interface NonceStore {

    fun store(key: String, nonce: String)

    fun load(key: String): String
}