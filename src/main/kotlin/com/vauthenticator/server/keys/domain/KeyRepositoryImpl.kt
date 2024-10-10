package com.vauthenticator.server.keys.domain

import java.time.Duration

class KeyRepositoryImpl(
    private val kidGenerator: () -> String,
    private val keyStorage : KeyStorage,
    private val keyGenerator: KeyGenerator,
) : KeyRepository {


    override fun createKeyFrom(masterKid: MasterKid, keyType: KeyType, keyPurpose: KeyPurpose): Kid {
        val dataKey = keyPairFor(masterKid, keyType)
        val kidContent = kidGenerator.invoke()

        keyStorage.store(masterKid, kidContent, dataKey, keyType, keyPurpose)

        return Kid(kidContent)
    }

    private fun keyPairFor(masterKid: MasterKid, keyType: KeyType) =
        if (keyType == KeyType.ASYMMETRIC) {
            keyGenerator.dataKeyPairFor(masterKid)
        } else {
            keyGenerator.dataKeyFor(masterKid)
        }

    override fun deleteKeyFor(kid: Kid, keyPurpose: KeyPurpose, ttl: Duration) {
        val key = keyFor(kid, keyPurpose)
        if (!key.enabled){
            throw KeyDeletionException(
                "The key with kid: $kid is a rotated key.... it can not be deleted or rotated again." +
                        " Let's wait for the expiration time." +
                        " This key is not enabled to sign new token, only to verify already signed token before the rotation request"
            )
        }
        val keys = validSignatureKeys()

        val noTtl = ttl.isZero

        if (noTtl) {
            if (keyPurpose == KeyPurpose.SIGNATURE && keys.size <= 1) {
                throw KeyDeletionException("at least one signature key is mandatory")
            }

            keyStorage.justDeleteKey(kid, keyPurpose)
        } else {
            keyStorage.keyDeleteJodPlannedFor(kid, ttl, keyPurpose)
        }
    }

    private fun validSignatureKeys() = Keys(signatureKeys().keys).validKeys().keys


    override fun signatureKeys(): Keys {
        return keyStorage.signatureKeys()
    }

    override fun keyFor(kid: Kid, keyPurpose: KeyPurpose): Key {
        return keyStorage.findOne(kid, keyPurpose)
    }

}