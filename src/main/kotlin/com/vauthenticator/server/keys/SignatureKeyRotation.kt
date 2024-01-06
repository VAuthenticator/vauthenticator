package com.vauthenticator.server.keys

import java.time.Duration

class SignatureKeyRotation(private val keyRepository: KeyRepository) {
    fun rotate(masterKid: MasterKid, kid: Kid, ttl: Duration): Kid {
        keyRepository.deleteKeyFor(kid, KeyPurpose.SIGNATURE, ttl)
        return keyRepository.createKeyFrom(masterKid)
    }
}