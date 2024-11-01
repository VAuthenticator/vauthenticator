package com.vauthenticator.server.keys.adapter.local

import com.vauthenticator.server.extentions.encoder
import com.vauthenticator.server.keys.domain.KeyDecrypter
import com.vauthenticator.server.keys.domain.MasterKid

class BouncyCastleKeyDecrypter(private val keyCryptographicOperations: KeyCryptographicOperations) : KeyDecrypter {
    override fun decryptKey(encrypted: String): String {
        return encoder.encode(keyCryptographicOperations.decryptKeyWith(MasterKid(""), encrypted.toByteArray()))
            .decodeToString()
    }
}