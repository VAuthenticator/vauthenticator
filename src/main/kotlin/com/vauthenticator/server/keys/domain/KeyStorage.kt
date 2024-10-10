package com.vauthenticator.server.keys.domain

import java.time.Duration

interface KeyStorage {
    fun store(masterKid: MasterKid, kidContent: String, dataKey: DataKey, keyType: KeyType, keyPurpose: KeyPurpose)

    fun signatureKeys(): Keys

    fun findOne(kid: Kid, keyPurpose: KeyPurpose): Key

    fun justDeleteKey(kid: Kid, keyPurpose: KeyPurpose)

    fun keyDeleteJodPlannedFor(kid: Kid, ttl: Duration, keyPurpose: KeyPurpose)
}