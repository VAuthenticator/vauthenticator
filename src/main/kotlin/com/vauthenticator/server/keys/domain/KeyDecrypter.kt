package com.vauthenticator.server.keys.domain

interface KeyDecrypter {
    fun decryptKey(encrypted: String): String
}

