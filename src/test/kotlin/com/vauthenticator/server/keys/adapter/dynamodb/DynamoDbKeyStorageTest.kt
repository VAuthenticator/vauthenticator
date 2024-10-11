package com.vauthenticator.server.keys.adapter.dynamodb

import com.vauthenticator.server.extentions.asDynamoAttribute
import com.vauthenticator.server.extentions.encoder
import com.vauthenticator.server.extentions.valueAsStringFor
import com.vauthenticator.server.keys.adapter.dynamo.DynamoDbKeyStorage
import com.vauthenticator.server.keys.domain.KeyPurpose.MFA
import com.vauthenticator.server.keys.domain.KeyPurpose.SIGNATURE
import com.vauthenticator.server.keys.domain.KeyStorage
import com.vauthenticator.server.keys.domain.KeyType.ASYMMETRIC
import com.vauthenticator.server.keys.domain.KeyType.SYMMETRIC
import com.vauthenticator.server.keys.domain.Keys
import com.vauthenticator.server.keys.domain.Kid
import com.vauthenticator.server.support.DynamoDbUtils.dynamoDbClient
import com.vauthenticator.server.support.DynamoDbUtils.dynamoMfaKeysTableName
import com.vauthenticator.server.support.DynamoDbUtils.dynamoSignatureKeysTableName
import com.vauthenticator.server.support.DynamoDbUtils.resetDynamoDb
import com.vauthenticator.server.support.KeysUtils.aKeyFor
import com.vauthenticator.server.support.KeysUtils.aKid
import com.vauthenticator.server.support.KeysUtils.aMasterKey
import com.vauthenticator.server.support.KeysUtils.aSignatureDataKey
import com.vauthenticator.server.support.KeysUtils.aSimmetricDataKey
import com.vauthenticator.server.support.KeysUtils.anotherKid
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

class DynamoDbKeyStorageTest {

    val masterKid = aMasterKey
    private lateinit var uut: KeyStorage

    private val now = Instant.now()

    @BeforeEach
    fun setUp() {
        resetDynamoDb()
        uut = DynamoDbKeyStorage(
            Clock.fixed(now, ZoneId.systemDefault()),
            dynamoDbClient,
            dynamoSignatureKeysTableName,
            dynamoMfaKeysTableName
        )
    }

    @Test
    fun `when store a new data key pair`() {
        uut.store(masterKid, aKid, aSignatureDataKey, ASYMMETRIC, SIGNATURE)

        val actual = getActual(aKid, dynamoSignatureKeysTableName)
        assertEquals(aKid.content(), actual.valueAsStringFor("key_id"))
        assertEquals(masterKid.content(), actual.valueAsStringFor("master_key_id"))
        assertEquals(
            encoder.encode(aSignatureDataKey.encryptedPrivateKey)
                .decodeToString(), actual.valueAsStringFor("encrypted_private_key")
        )
        assertEquals(
            encoder.encode(aSignatureDataKey.publicKey.get()).decodeToString(),
            actual.valueAsStringFor("public_key")
        )
    }

    @Test
    fun `when store a new data key for mfa`() {
        uut.store(masterKid, aKid, aSimmetricDataKey, SYMMETRIC, MFA)

        val actual = getActual(aKid, dynamoMfaKeysTableName)
        assertEquals(aKid.content(), actual.valueAsStringFor("key_id"))
        assertEquals(masterKid.content(), actual.valueAsStringFor("master_key_id"))
        assertEquals(
            encoder.encode(aSimmetricDataKey.encryptedPrivateKey)
                .decodeToString(), actual.valueAsStringFor("encrypted_private_key")
        )
    }

    @Test
    fun `when find an mfa key`() {
        uut.store(masterKid, aKid, aSimmetricDataKey, SYMMETRIC, MFA)
        val actual = uut.findOne(aKid, MFA)
        val excpected = aKeyFor(masterKid.content(), aKid.content(), SYMMETRIC, MFA)
        assertEquals(excpected, actual)
    }

    @Test
    fun `when find an signature key`() {
        uut.store(masterKid, aKid, aSignatureDataKey, ASYMMETRIC, SIGNATURE)
        val actual = uut.findOne(aKid, SIGNATURE)
        val excpected = aKeyFor(masterKid.content(), aKid.content(), ASYMMETRIC, SIGNATURE)
        assertEquals(excpected, actual)
    }

    @Test
    fun `when find all signature keys`() {
        uut.store(masterKid, aKid, aSignatureDataKey, ASYMMETRIC, SIGNATURE)
        uut.store(masterKid, anotherKid, aSignatureDataKey, ASYMMETRIC, SIGNATURE)
        val actual = uut.signatureKeys()
        val excpected = Keys(
            listOf(
                aKeyFor(masterKid.content(), aKid.content(), ASYMMETRIC, SIGNATURE),
                aKeyFor(masterKid.content(), anotherKid.content(), ASYMMETRIC, SIGNATURE)
            )
        )
        assertEquals(excpected, actual)
    }

    /*


        @Test
        fun `when a signature key is deleted`() {
            val masterKid = aNewMasterKey()
            val kid = keyRepository.createKeyFrom(masterKid)
            keyRepository.createKeyFrom(masterKid)
            val ttl = Duration.ofSeconds(1)
            keyRepository.deleteKeyFor(kid, SIGNATURE, ttl)

            val actual = getActual(kid, dynamoSignatureKeysTableName)
            val expectedTTl = now.epochSecond + 1
            assertEquals(false, (actual["enabled"] as AttributeValue).bool())
            assertEquals(expectedTTl, (actual["key_expiration_date_timestamp"] as AttributeValue).n().toLong())
        }

        @Test
        fun `when delete a rotated signature key`() {
            val masterKid = aNewMasterKey()
            val kid = keyRepository.createKeyFrom(masterKid)
            keyRepository.createKeyFrom(masterKid)
            keyRepository.createKeyFrom(masterKid)
            keyRepository.createKeyFrom(masterKid)
            keyRepository.createKeyFrom(masterKid)
            val ttl = Duration.ofSeconds(1)
            keyRepository.deleteKeyFor(kid, SIGNATURE, ttl)

            val actual = getActual(kid, dynamoSignatureKeysTableName)
            val expectedTTl = now.epochSecond + 1
            assertEquals(false, (actual["enabled"] as AttributeValue).bool())
            assertEquals(expectedTTl, (actual["key_expiration_date_timestamp"] as AttributeValue).n().toLong())

            assertThrows(KeyDeletionException::class.java) {
                keyRepository.deleteKeyFor(kid, SIGNATURE)
            }
        }

        @Test
        fun `when a signature key deleted request is ignored due to only one valid key is left`() {
            val masterKid = aNewMasterKey()
            val firstKid = keyRepository.createKeyFrom(masterKid)
            val secondKid = keyRepository.createKeyFrom(masterKid)

            val ttl = Duration.ofSeconds(10)
            keyRepository.deleteKeyFor(firstKid, SIGNATURE, ttl)

            assertThrows(KeyDeletionException::class.java) {
                keyRepository.deleteKeyFor(secondKid, SIGNATURE)
            }

        }

        @Test
        fun `when a signature key deletion goes in error`() {
            val masterKid = aNewMasterKey()
            val kid = keyRepository.createKeyFrom(masterKid)

            assertThrows(KeyDeletionException::class.java) { keyRepository.deleteKeyFor(kid, SIGNATURE) }
        }

        @Test
        fun `when a mfa key is deleted`() {
            val masterKid = aNewMasterKey()
            val kid = keyRepository.createKeyFrom(masterKid = masterKid, keyPurpose = MFA)
            keyRepository.deleteKeyFor(kid, MFA)

            val actual = getActual(kid, dynamoMfaKeysTableName)
            assertEquals(emptyMap<String, AttributeValue>(), actual)
        }




        @Test
        fun `when a key is found by id and purpose`() {
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
                MFA,
                0L
            )

            assertEquals(expected, actual)
        }
    */

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
}