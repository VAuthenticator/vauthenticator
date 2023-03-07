package com.vauthenticator.server.keys

import software.amazon.awssdk.services.kms.KmsClient
import software.amazon.awssdk.services.kms.model.DataKeyPairSpec
import software.amazon.awssdk.services.kms.model.DataKeySpec
import software.amazon.awssdk.services.kms.model.GenerateDataKeyPairRequest
import software.amazon.awssdk.services.kms.model.GenerateDataKeyRequest
import java.security.KeyPairGenerator
import java.security.spec.KeySpec
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec


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

class OnPremiseKeyGenerator(private val symmetricKey: String) : KeyGenerator {

    companion object {
        fun keyFromPassword(password: String): SecretKey {
            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            val spec: KeySpec = PBEKeySpec(password.toCharArray())
            return SecretKeySpec(factory.generateSecret(spec).encoded, "AES")
        }
    }
    override fun dataKeyPairFor(masterKid: MasterKid) = TODO()

    override fun dataKeyFor(masterKid: MasterKid): DataKey = try {
        val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
        keyPairGenerator.initialize(2048)
        keyPairGenerator.generateKeyPair()
    } catch (ex: Exception) {
        throw IllegalStateException(ex)
    }.let { keyPair ->
        DataKey(
            encrypt(keyPair.private.encoded),
            Optional.ofNullable(keyPair.public.encoded)
        )
    }

    private fun encrypt(encoded: ByteArray): ByteArray = keyFromPassword(this.symmetricKey)
        .let {
            val cipher = Cipher.getInstance(it.algorithm)
            cipher.init(Cipher.ENCRYPT_MODE, it)
            cipher.doFinal(encoded)
        }


}

//fun generateSecretKey(): SecretKey? {
//    val hmacKey: SecretKey
//    hmacKey = try {
//        KeyGenerator.getInstance("HmacSha256").generateKey()
//    } catch (ex: Exception) {
//        throw IllegalStateException(ex)
//    }
//    return hmacKey
//}