package com.vauthenticator.server.keys.domain

import java.time.Duration

private val DELETE_NOW = Duration.ofSeconds(0)

interface KeyRepository {
    fun createKeyFrom(
        masterKid: MasterKid,
        keyType: KeyType = KeyType.ASYMMETRIC,
        keyPurpose: KeyPurpose = KeyPurpose.SIGNATURE
    ): Kid

    fun deleteKeyFor(kid: Kid, keyPurpose: KeyPurpose, ttl: Duration = DELETE_NOW)

    fun signatureKeys(): Keys

    fun keyFor(kid: Kid, mfa: KeyPurpose): Key
}


