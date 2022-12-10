package it.valeriovaudi.vauthenticator.keys

import it.valeriovaudi.vauthenticator.extentions.asDynamoAttribute
import it.valeriovaudi.vauthenticator.extentions.encoder
import it.valeriovaudi.vauthenticator.extentions.valueAsStringFor
import it.valeriovaudi.vauthenticator.keys.KeyPurpose.MFA
import it.valeriovaudi.vauthenticator.keys.KeyType.ASYMMETRIC
import it.valeriovaudi.vauthenticator.keys.KeyType.SYMMETRIC
import it.valeriovaudi.vauthenticator.support.DatabaseUtils.dynamoDbClient
import it.valeriovaudi.vauthenticator.support.DatabaseUtils.dynamoMfaKeysTableName
import it.valeriovaudi.vauthenticator.support.DatabaseUtils.dynamoSignatureKeysTableName
import it.valeriovaudi.vauthenticator.support.DatabaseUtils.resetDatabase
import it.valeriovaudi.vauthenticator.support.KeysUtils.aNewMasterKey
import it.valeriovaudi.vauthenticator.support.KeysUtils.kmsClient
import it.valeriovaudi.vauthenticator.support.KmsClientWrapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest

internal class AwsKeyRepositoryTest {

    private lateinit var keyRepository: KeyRepository
    private lateinit var wrapper: KmsClientWrapper

    private val kidGenerator = { "KID" }

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
        val kid = kidGenerator.invoke()

        keyRepository.createKeyFrom(masterKid, ASYMMETRIC)

        val actual = getActual(Kid(kid), dynamoSignatureKeysTableName)
        assertEquals(kid, actual.valueAsStringFor("key_id"))
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
        val kid = kidGenerator.invoke()

        keyRepository.createKeyFrom(masterKid, SYMMETRIC)

        val actual = getActual(Kid(kid), dynamoSignatureKeysTableName)
        assertEquals(kid, actual.valueAsStringFor("key_id"))
        assertEquals(masterKid.content(), actual.valueAsStringFor("master_key_id"))
        assertEquals(
            encoder.encode(wrapper.generateDataKeyRecorder.get().ciphertextBlob().asByteArray())
                .decodeToString(), actual.valueAsStringFor("encrypted_private_key")
        )
    }

    @Test
    internal fun `when create a new data key for mfa`() {
        val masterKid = aNewMasterKey()
        val kid = kidGenerator.invoke()

        keyRepository.createKeyFrom(masterKid, SYMMETRIC, MFA)

        val actual = getActual(Kid(kid), dynamoMfaKeysTableName)
        assertEquals(kid, actual.valueAsStringFor("key_id"))
        assertEquals(masterKid.content(), actual.valueAsStringFor("master_key_id"))
        assertEquals(
            encoder.encode(wrapper.generateDataKeyRecorder.get().ciphertextBlob().asByteArray())
                .decodeToString(), actual.valueAsStringFor("encrypted_private_key")
        )
    }

    @Test
    internal fun `when a signature key is deleted`() {
        val kid = Kid(kidGenerator.invoke())
        keyRepository.deleteKeyFor(kid, KeyPurpose.SIGNATURE)

        val actual = getActual(kid, dynamoSignatureKeysTableName)
        assertEquals(emptyMap<String, AttributeValue>(), actual)
    }

    @Test
    internal fun `when a mfa key is deleted`() {
        val kid = Kid(kidGenerator.invoke())
        keyRepository.deleteKeyFor(kid, MFA)

        val actual = getActual(kid, dynamoMfaKeysTableName)
        assertEquals(emptyMap<String, AttributeValue>(), actual)
    }

    @Test
    internal fun `when a key is deleted after a brand new insert`() {
        val masterKid = aNewMasterKey()
        val kid = Kid(kidGenerator.invoke())

        keyRepository.createKeyFrom(masterKid)
        keyRepository.deleteKeyFor(kid, KeyPurpose.SIGNATURE)

        val actual = getActual(kid, dynamoSignatureKeysTableName)
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
        val kid = Kid(kidGenerator.invoke())

        keyRepository.createKeyFrom(masterKid, SYMMETRIC, MFA)

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