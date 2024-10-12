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
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import kotlin.test.assertNotNull

class DynamoDbKeyStorageTest {

    private val masterKid = aMasterKey
    private val now = Instant.now()

    private lateinit var uut: KeyStorage

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

    @Test
    fun `when a signature key is deleted`() {
        uut.store(masterKid, aKid, aSignatureDataKey, ASYMMETRIC, SIGNATURE)
        val storedKey = uut.findOne(aKid, SIGNATURE)
        assertNotNull(storedKey)

        uut.justDeleteKey(aKid, SIGNATURE)
        val actual = getActual(aKid, dynamoSignatureKeysTableName)
        assertEquals(emptyMap<String, AttributeValue>(), actual)
    }

    @Test
    fun `when a signature key planned  to be deleted`() {
        uut.store(masterKid, aKid, aSignatureDataKey, ASYMMETRIC, SIGNATURE)
        val storedKey = uut.findOne(aKid, SIGNATURE)
        assertNotNull(storedKey)

        val ttl = Duration.ofSeconds(1)
        uut.keyDeleteJodPlannedFor(aKid, ttl, SIGNATURE)

        val actual = getActual(aKid, dynamoSignatureKeysTableName)
        val expectedTTl = now.epochSecond + 1
        assertEquals(false, (actual["enabled"] as AttributeValue).bool())
        assertEquals(expectedTTl, (actual["key_expiration_date_timestamp"] as AttributeValue).n().toLong())
    }

    @Test
    fun `when a signature key deletion planning is ignored`() {
        uut.store(masterKid, aKid, aSignatureDataKey, ASYMMETRIC, SIGNATURE)
        val storedKey = uut.findOne(aKid, SIGNATURE)
        assertNotNull(storedKey)

        uut.keyDeleteJodPlannedFor(aKid, Duration.ofSeconds(1), SIGNATURE)

        val actual = getActual(aKid, dynamoSignatureKeysTableName)
        val expectedTTl = now.epochSecond + 1
        assertEquals(false, (actual["enabled"] as AttributeValue).bool())
        assertEquals(expectedTTl, (actual["key_expiration_date_timestamp"] as AttributeValue).n().toLong())

        uut.keyDeleteJodPlannedFor(aKid, Duration.ofSeconds(10), SIGNATURE)

        val actualAfterReplanning = getActual(aKid, dynamoSignatureKeysTableName)
        val expectedTTlAfterReplanning = now.epochSecond + 1
        assertEquals(false, (actualAfterReplanning["enabled"] as AttributeValue).bool())
        assertEquals(expectedTTlAfterReplanning, (actualAfterReplanning["key_expiration_date_timestamp"] as AttributeValue).n().toLong())
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
}