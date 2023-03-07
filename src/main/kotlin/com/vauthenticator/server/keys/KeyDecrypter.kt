package com.vauthenticator.server.keys

import com.vauthenticator.server.extentions.decoder
import com.vauthenticator.server.extentions.encoder
import software.amazon.awssdk.core.SdkBytes.fromByteArray
import software.amazon.awssdk.services.kms.KmsClient
import software.amazon.awssdk.services.kms.model.DecryptRequest
import javax.crypto.Cipher

interface KeyDecrypter {
    fun decryptKey(encrypted: String): String
}

class KmsKeyDecrypter(private val kmsClient: KmsClient) : KeyDecrypter {
    override fun decryptKey(privateKey: String): String = kmsClient.decrypt(
        DecryptRequest.builder()
            .ciphertextBlob(fromByteArray(decoder.decode(privateKey)))
            .build()
    ).let {
        encoder.encode(it.plaintext().asByteArray()).decodeToString()
    }
}

class OnPremiseKeyDecrypter(private val symmetricKey: String) : KeyDecrypter {
    override fun decryptKey(privateKey: String): String {
        val secretKey = OnPremiseKeyGenerator.keyFromPassword(symmetricKey)
        val cipher: Cipher = Cipher.getInstance(secretKey.algorithm)
        cipher.init(Cipher.DECRYPT_MODE, secretKey)
        val plainText: ByteArray = cipher.doFinal(decoder.decode(privateKey))
        return encoder.encode(plainText).decodeToString()
    }
}