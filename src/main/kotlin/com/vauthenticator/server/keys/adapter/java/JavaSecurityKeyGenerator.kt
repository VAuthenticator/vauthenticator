package com.vauthenticator.server.keys.adapter.java

import com.vauthenticator.server.keys.domain.DataKey
import com.vauthenticator.server.keys.domain.KeyGenerator
import com.vauthenticator.server.keys.domain.MasterKid
import java.util.*


class JavaSecurityKeyGenerator(
    private val keyCryptographicOperations: KeyCryptographicOperations
) : KeyGenerator {


    override fun dataKeyPairFor(masterKid: MasterKid): DataKey {
        val generateRSAKeyPair = keyCryptographicOperations.generateRSAKeyPair()
        return DataKey(
            keyCryptographicOperations.encryptKeyWith(masterKid, generateRSAKeyPair.private.encoded),
            Optional.of(generateRSAKeyPair.public.encoded)
        )
    }

    override fun dataKeyFor(masterKid: MasterKid): DataKey {
        val generateRSAKeyPair = keyCryptographicOperations.generateRSAKeyPair()
        return DataKey(
            keyCryptographicOperations.encryptKeyWith(masterKid, generateRSAKeyPair.private.encoded),
            Optional.empty()
        )
    }



}