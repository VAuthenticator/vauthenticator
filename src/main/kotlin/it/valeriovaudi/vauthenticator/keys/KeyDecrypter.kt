package it.valeriovaudi.vauthenticator.keys

import it.valeriovaudi.vauthenticator.extentions.decoder
import it.valeriovaudi.vauthenticator.extentions.encoder
import software.amazon.awssdk.core.SdkBytes
import software.amazon.awssdk.core.SdkBytes.*
import software.amazon.awssdk.services.kms.KmsClient
import software.amazon.awssdk.services.kms.model.DecryptRequest

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