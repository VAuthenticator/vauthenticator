package com.vauthenticator.server.keys.domain

import com.vauthenticator.server.keys.KeyPurpose
import com.vauthenticator.server.keys.Kid
import com.vauthenticator.server.keys.MasterKid
import java.time.Duration

class SignatureKeyRotation(private val keyRepository: KeyRepository) {
    fun rotate(masterKid: MasterKid, kid: Kid, ttl: Duration): Kid {
        val newKey = keyRepository.createKeyFrom(masterKid);
        keyRepository.deleteKeyFor(kid, KeyPurpose.SIGNATURE, ttl)
        return newKey
    }
}