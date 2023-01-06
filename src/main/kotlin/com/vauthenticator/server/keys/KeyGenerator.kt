package com.vauthenticator.server.keys

import software.amazon.awssdk.services.kms.KmsClient
import software.amazon.awssdk.services.kms.model.DataKeyPairSpec
import software.amazon.awssdk.services.kms.model.DataKeySpec
import software.amazon.awssdk.services.kms.model.GenerateDataKeyPairRequest
import software.amazon.awssdk.services.kms.model.GenerateDataKeyRequest
import java.util.*

interface KeyGenerator {
    fun dataKeyPairFor(masterKid: MasterKid): DataKey
    fun dataKeyFor(masterKid: MasterKid): DataKey
}

class KmsKeyGenerator(private val kmsClient: KmsClient) : KeyGenerator {

    override fun dataKeyPairFor(masterKid: MasterKid) = kmsClient.generateDataKeyPair(
        GenerateDataKeyPairRequest.builder()
            .keyId(masterKid.content())
            .keyPairSpec(DataKeyPairSpec.RSA_2048)
            .build()
    ).let {
        DataKey(
            encryptedPrivateKey = it.privateKeyCiphertextBlob().asByteArray(),
            publicKey = Optional.of(it.publicKey().asByteArray())
        )
    }

    override fun dataKeyFor(masterKid: MasterKid): DataKey = kmsClient.generateDataKey(
        GenerateDataKeyRequest.builder().keyId(masterKid.content()).keySpec(DataKeySpec.AES_256).build()
    ).let {
        DataKey(
            encryptedPrivateKey = it.ciphertextBlob().asByteArray(),
            publicKey = Optional.empty<ByteArray>()
        )
    }

}