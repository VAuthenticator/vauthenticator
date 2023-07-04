package com.vauthenticator.server.keys

import com.vauthenticator.server.extentions.asDynamoAttribute
import com.vauthenticator.server.extentions.valueAsBoolFor
import com.vauthenticator.server.extentions.valueAsStringFor
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.*

interface KeyRepository {
    fun createKeyFrom(
        masterKid: MasterKid,
        keyType: KeyType = KeyType.ASYMMETRIC,
        keyPurpose: KeyPurpose = KeyPurpose.SIGNATURE
    ): Kid

    fun deleteKeyFor(kid: Kid, keyPurpose: KeyPurpose)
    fun signatureKeys(): Keys
    fun keyFor(kid: Kid, mfa: KeyPurpose): Key
}


open class AwsKeyRepository(
    private val kidGenerator: () -> String,
    private val signatureTableName: String,
    private val mfaTableName: String,
    private val keyGenerator: KeyGenerator,
    private val dynamoDbClient: DynamoDbClient
) : KeyRepository {

    override fun createKeyFrom(masterKid: MasterKid, keyType: KeyType, keyPurpose: KeyPurpose): Kid {
        val dataKey = keyPairFor(masterKid, keyType)
        val kidContent = kidGenerator.invoke()

        val tableName = tableNameBasedOn(keyPurpose)

        storeKeyOnDynamo(masterKid, kidContent, dataKey, keyType, keyPurpose, tableName)

        return Kid(kidContent)
    }

    private fun storeKeyOnDynamo(
        masterKid: MasterKid,
        kidContent: String,
        dataKey: DataKey,
        keyType: KeyType,
        keyPurpose: KeyPurpose,
        tableName: String
    ) {
        dynamoDbClient.putItem(
            PutItemRequest.builder()
                .tableName(tableName)
                .item(
                    mapOf(
                        "master_key_id" to masterKid.content().asDynamoAttribute(),
                        "key_id" to kidContent.asDynamoAttribute(),
                        "encrypted_private_key" to dataKey.encryptedPrivateKeyAsString().asDynamoAttribute(),
                        "public_key" to dataKey.publicKeyAsString().asDynamoAttribute(),
                        "key_purpose" to keyPurpose.name.asDynamoAttribute(),
                        "key_type" to keyType.name.asDynamoAttribute(),
                        "enabled" to true.asDynamoAttribute()
                    )
                ).build()
        )
    }

    private fun keyPairFor(masterKid: MasterKid, keyType: KeyType) =
        if (keyType == KeyType.ASYMMETRIC) {
            keyGenerator.dataKeyPairFor(masterKid)
        } else {
            keyGenerator.dataKeyFor(masterKid)
        }

    override fun deleteKeyFor(kid: Kid, keyPurpose: KeyPurpose) {
        val keys = signatureKeys().keys
        if (keyPurpose == KeyPurpose.SIGNATURE && keys.size <= 1) {
            throw KeyDeletionException("at least one signature key is mandatory")
        }

        val tableName = tableNameBasedOn(keyPurpose)
        dynamoDbClient.deleteItem(
            DeleteItemRequest.builder().tableName(tableName).key(
                mapOf(
                    "key_id" to kid.content().asDynamoAttribute(),
                )
            ).build()
        )

    }

    override fun signatureKeys(): Keys {
        val keysOnDynamo = findAllFrom(signatureTableName)
        val items = keysOnDynamo.items()
        val keys = keysListFrom(items)
        return Keys(keys)
    }

    override fun keyFor(kid: Kid, keyPurpose: KeyPurpose): Key {
        val tableName = tableNameBasedOn(keyPurpose)
        return dynamoDbClient.getItem(
            GetItemRequest.builder().tableName(tableName).key(
                mapOf(
                    "key_id" to kid.content().asDynamoAttribute()
                )
            ).build()
        ).item().let { keyFromDynamoFor(it) }
    }

    private fun tableNameBasedOn(keyPurpose: KeyPurpose) = when (keyPurpose) {
        KeyPurpose.MFA -> mfaTableName
        KeyPurpose.SIGNATURE -> signatureTableName
    }

    private fun findAllFrom(table: String) = dynamoDbClient.scan(
        ScanRequest.builder().tableName(table).build()
    )

    private fun keysListFrom(items: MutableList<MutableMap<String, AttributeValue>>) =
        items.map { keyFromDynamoFor(it) }

    private fun keyFromDynamoFor(it: MutableMap<String, AttributeValue>) = Key(
        DataKey.from(
            it.valueAsStringFor("encrypted_private_key"), it.valueAsStringFor("public_key")
        ),
        MasterKid(it.valueAsStringFor("master_key_id")),
        Kid(it.valueAsStringFor("key_id")),
        it.valueAsBoolFor("enabled"),
        KeyType.valueOf(it.valueAsStringFor("key_type")),
        KeyPurpose.valueOf(it.valueAsStringFor("key_purpose"))
    )

}