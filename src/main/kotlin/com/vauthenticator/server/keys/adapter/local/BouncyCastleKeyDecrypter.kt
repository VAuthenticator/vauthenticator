package com.vauthenticator.server.keys.adapter.local

import com.vauthenticator.server.extentions.encoder
import com.vauthenticator.server.keys.domain.KeyDecrypter
import com.vauthenticator.server.keys.domain.MasterKid
import org.springframework.beans.factory.annotation.Value

class BouncyCastleKeyDecrypter(
    private val maserKid: String,
    private val keyCryptographicOperations: KeyCryptographicOperations
) : KeyDecrypter {
    override fun decryptKey(encrypted: String): String {
        return encoder.encode(keyCryptographicOperations.decryptKeyWith(MasterKid(maserKid), encrypted.toByteArray()))
            .decodeToString()
    }
}