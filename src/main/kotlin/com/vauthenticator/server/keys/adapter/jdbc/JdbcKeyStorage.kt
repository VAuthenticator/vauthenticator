package com.vauthenticator.server.keys.adapter.jdbc

import com.vauthenticator.server.keys.domain.*
import org.slf4j.LoggerFactory
import java.time.Duration

class JdbcKeyStorage : KeyStorage {

    private val logger = LoggerFactory.getLogger(JdbcKeyStorage::class.java)

    override fun store(
        masterKid: MasterKid,
        kid: Kid,
        dataKey: DataKey,
        keyType: KeyType,
        keyPurpose: KeyPurpose
    ) {
        TODO()
    }

    override fun signatureKeys(): Keys {
        TODO()
    }

    override fun findOne(kid: Kid, keyPurpose: KeyPurpose): Key {
        TODO()
    }

    override fun justDeleteKey(kid: Kid, keyPurpose: KeyPurpose) {
        TODO()
    }

    override fun keyDeleteJodPlannedFor(kid: Kid, ttl: Duration, keyPurpose: KeyPurpose) {
        TODO()
    }

}