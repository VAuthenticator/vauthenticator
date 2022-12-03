package it.valeriovaudi.vauthenticator.keypair

import it.valeriovaudi.vauthenticator.extentions.asDynamoAttribute
import it.valeriovaudi.vauthenticator.extentions.encoder
import it.valeriovaudi.vauthenticator.extentions.valueAsStringFor
import it.valeriovaudi.vauthenticator.support.DatabaseUtils.dynamoDbClient
import it.valeriovaudi.vauthenticator.support.DatabaseUtils.dynamoKeysTableName
import it.valeriovaudi.vauthenticator.support.DatabaseUtils.resetDatabase
import it.valeriovaudi.vauthenticator.support.KeysUtils.aNewMasterKey
import it.valeriovaudi.vauthenticator.support.KeysUtils.kmsClient
import it.valeriovaudi.vauthenticator.support.KmsClientWrapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest

internal class DynamoKeyRepositoryTest {

    private lateinit var keyRepository: KeyRepository

    private val kidGenerator = { "KID" }

    @BeforeEach
    internal fun setUp() {
        resetDatabase()
    }

    @Test
    internal fun `when create a new key`() {
        val masterKid = aNewMasterKey()
        val kid = kidGenerator.invoke()
        val wrapper = KmsClientWrapper(kmsClient)
        keyRepository =
            DynamoKeyRepository(kidGenerator, dynamoKeysTableName, KmsKeyRepository(wrapper), dynamoDbClient)

        keyRepository.createKeyFrom(masterKid)


        val actual = dynamoDbClient.getItem(
            GetItemRequest.builder().tableName(dynamoKeysTableName)
                .key(mapOf("master_key_id" to masterKid.content().asDynamoAttribute(), "key_id" to kid.asDynamoAttribute()))
                .build()
        ).item()
        assertEquals(kid, actual.valueAsStringFor("key_id"))
        assertEquals(masterKid.content(), actual.valueAsStringFor("master_key_id"))
        assertEquals(
            encoder.encode(wrapper.generateDataKeyPairRecorder.get().privateKeyCiphertextBlob().asByteArray())
                .decodeToString(), actual.valueAsStringFor("private_key_ciphertext_blob")
        )
        assertEquals(
            encoder.encode(wrapper.generateDataKeyPairRecorder.get().publicKey().asByteArray()).decodeToString(),
            actual.valueAsStringFor("public_key")
        )
    }

    @Test
    internal fun `when a key is deleted`() {
        val masterKid = aNewMasterKey()
        val kid = Kid(kidGenerator.invoke())
        val wrapper = KmsClientWrapper(kmsClient)
        keyRepository =
            DynamoKeyRepository(kidGenerator, dynamoKeysTableName, KmsKeyRepository(wrapper), dynamoDbClient)

        keyRepository.deleteKeyFor(masterKid, kid)


        val actual = dynamoDbClient.getItem(
            GetItemRequest.builder().tableName(dynamoKeysTableName)
                .key(mapOf("master_key_id" to masterKid.content().asDynamoAttribute(), "key_id" to kid.content().asDynamoAttribute()))
                .build()
        ).item()
        assertEquals(emptyMap<String, AttributeValue>(), actual)
    }

    @Test
    internal fun `when a key is deleted after a brand new insert`() {
        val masterKid = aNewMasterKey()
        val kid = Kid(kidGenerator.invoke())
        val wrapper = KmsClientWrapper(kmsClient)
        keyRepository =
            DynamoKeyRepository(kidGenerator, dynamoKeysTableName, KmsKeyRepository(wrapper), dynamoDbClient)

        keyRepository.createKeyFrom(masterKid)

        keyRepository.deleteKeyFor(masterKid, kid)


        val actual = dynamoDbClient.getItem(
            GetItemRequest.builder().tableName(dynamoKeysTableName)
                .key(mapOf("master_key_id" to masterKid.content().asDynamoAttribute(), "key_id" to kid.content().asDynamoAttribute()))
                .build()
        ).item()
        assertEquals(emptyMap<String, AttributeValue>(), actual)
    }

}