package it.valeriovaudi.vauthenticator.keys

import it.valeriovaudi.vauthenticator.extentions.decoder
import it.valeriovaudi.vauthenticator.extentions.encoder
import it.valeriovaudi.vauthenticator.support.KeysUtils
import it.valeriovaudi.vauthenticator.support.KeysUtils.aNewMasterKey
import it.valeriovaudi.vauthenticator.support.KeysUtils.kmsClient
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import software.amazon.awssdk.services.kms.model.CreateKeyRequest
import software.amazon.awssdk.services.kms.model.DataKeySpec
import software.amazon.awssdk.services.kms.model.GenerateDataKeyRequest
import java.util.*

internal class KmsKeyDecrypterTest {

    @Test
    internal fun `happy path`() {
        val kmsKeyDecrypter = KmsKeyDecrypter(kmsClient)
        val masterKey = aNewMasterKey()
        val dataKeyRequest = GenerateDataKeyRequest.builder()
            .keyId(masterKey.content())
            .keySpec(DataKeySpec.AES_256)
            .build()
        val kmsDataKey = kmsClient.generateDataKey(dataKeyRequest)
        val dataKey = DataKey(kmsDataKey.ciphertextBlob().asByteArray(), Optional.empty())

        val expected = encoder.encode(kmsDataKey.plaintext().asByteArray()).decodeToString()
        val actual = kmsKeyDecrypter.decryptKey(dataKey.encryptedPrivateKeyAsString())
        assertEquals(expected, actual)
    }
}