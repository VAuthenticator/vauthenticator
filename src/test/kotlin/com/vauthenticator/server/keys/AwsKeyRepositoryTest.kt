package com.vauthenticator.server.keys

import com.vauthenticator.server.extentions.asDynamoAttribute
import com.vauthenticator.server.extentions.encoder
import com.vauthenticator.server.extentions.valueAsStringFor
import com.vauthenticator.server.keys.KeyPurpose.MFA
import com.vauthenticator.server.keys.KeyPurpose.SIGNATURE
import com.vauthenticator.server.keys.KeyType.ASYMMETRIC
import com.vauthenticator.server.keys.KeyType.SYMMETRIC
import com.vauthenticator.server.support.DatabaseUtils.dynamoDbClient
import com.vauthenticator.server.support.DatabaseUtils.dynamoMfaKeysTableName
import com.vauthenticator.server.support.DatabaseUtils.dynamoSignatureKeysTableName
import com.vauthenticator.server.support.DatabaseUtils.resetDatabase
import com.vauthenticator.server.support.KeysUtils.aNewMasterKey
import com.vauthenticator.server.support.KeysUtils.kmsClient
import com.vauthenticator.server.support.KmsClientWrapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest
import java.time.Duration
import java.util.*

internal class AwsKeyRepositoryTest {

    private lateinit var keyRepository: KeyRepository
    private lateinit var wrapper: KmsClientWrapper

    private val kidGenerator = { UUID.randomUUID().toString() }

    @BeforeEach
    internal fun setUp() {
        resetDatabase()
        wrapper = KmsClientWrapper(kmsClient)
        keyRepository =
            AwsKeyRepository(
                kidGenerator,
                dynamoSignatureKeysTableName,
                dynamoMfaKeysTableName,
                KmsKeyGenerator(wrapper),
                dynamoDbClient
            )
    }

    @Test
    internal fun `when create a new data key pair`() {
        val masterKid = aNewMasterKey()

        val kid = keyRepository.createKeyFrom(masterKid, ASYMMETRIC)

        val actual = getActual(kid, dynamoSignatureKeysTableName)
        assertEquals(kid.content(), actual.valueAsStringFor("key_id"))
        assertEquals(masterKid.content(), actual.valueAsStringFor("master_key_id"))
        assertEquals(
            encoder.encode(wrapper.generateDataKeyPairRecorder.get().privateKeyCiphertextBlob().asByteArray())
                .decodeToString(), actual.valueAsStringFor("encrypted_private_key")
        )
        assertEquals(
            encoder.encode(wrapper.generateDataKeyPairRecorder.get().publicKey().asByteArray()).decodeToString(),
            actual.valueAsStringFor("public_key")
        )
    }

    @Test
    internal fun `when create a new data key`() {
        val masterKid = aNewMasterKey()
        val kid = keyRepository.createKeyFrom(masterKid, SYMMETRIC)

        val actual = getActual(kid, dynamoSignatureKeysTableName)
        assertEquals(kid.content(), actual.valueAsStringFor("key_id"))
        assertEquals(masterKid.content(), actual.valueAsStringFor("master_key_id"))
        assertEquals(
            encoder.encode(wrapper.generateDataKeyRecorder.get().ciphertextBlob().asByteArray())
                .decodeToString(), actual.valueAsStringFor("encrypted_private_key")
        )
    }

    @Test
    internal fun `when create a new data key for mfa`() {
        val masterKid = aNewMasterKey()
        val kid = keyRepository.createKeyFrom(masterKid, SYMMETRIC, MFA)

        val actual = getActual(kid, dynamoMfaKeysTableName)
        assertEquals(kid.content(), actual.valueAsStringFor("key_id"))
        assertEquals(masterKid.content(), actual.valueAsStringFor("master_key_id"))
        assertEquals(
            encoder.encode(wrapper.generateDataKeyRecorder.get().ciphertextBlob().asByteArray())
                .decodeToString(), actual.valueAsStringFor("encrypted_private_key")
        )
    }

    @Test
    internal fun `when a signature key is deleted`() {
        val masterKid = aNewMasterKey()
        val kid = keyRepository.createKeyFrom(masterKid)
        keyRepository.createKeyFrom(masterKid)
        val ttl = Duration.ofSeconds(1)
        keyRepository.deleteKeyFor(kid, SIGNATURE, ttl)

        val actual = getActual(kid, dynamoSignatureKeysTableName)
        assertEquals(false, (actual["enabled"] as AttributeValue).bool())
        assertEquals(ttl.seconds, (actual["key_ttl"] as AttributeValue).n().toLong())
    }

    @Test
    internal fun `when a signature key deletion goes in error`() {
        val masterKid = aNewMasterKey()
        val kid = keyRepository.createKeyFrom(masterKid)

        assertThrows(KeyDeletionException::class.java) { keyRepository.deleteKeyFor(kid, SIGNATURE) }
    }

    @Test
    internal fun `when a mfa key is deleted`() {
        val kid = Kid(kidGenerator.invoke())
        keyRepository.deleteKeyFor(kid, MFA)

        val actual = getActual(kid, dynamoMfaKeysTableName)
        assertEquals(emptyMap<String, AttributeValue>(), actual)
    }


    private fun getActual(kid: Kid, tableName: String): MutableMap<String, AttributeValue> =
        dynamoDbClient.getItem(
            GetItemRequest.builder().tableName(tableName)
                .key(
                    mapOf(
                        "key_id" to kid.content().asDynamoAttribute()
                    )
                )
                .build()
        ).item()

    @Test
    internal fun `when a key is found by id and purpose`() {
        val masterKid = aNewMasterKey()
        val kid = keyRepository.createKeyFrom(masterKid, SYMMETRIC, MFA)

        val actual = keyRepository.keyFor(kid, MFA)
        val expected = Key(
            DataKey.from(
                encoder.encode(wrapper.generateDataKeyRecorder.get().ciphertextBlob().asByteArray())
                    .decodeToString(),
                ""
            ),
            masterKid,
            kid,
            true,
            SYMMETRIC,
            MFA
        )

        assertEquals(expected, actual)
    }

}