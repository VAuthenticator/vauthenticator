package com.vauthenticator.server.keys

import com.vauthenticator.server.extentions.*
import org.slf4j.LoggerFactory
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.*
import java.time.Clock
import java.time.Duration

private val DELETE_NOW = Duration.ofSeconds(0)

interface KeyRepository {
    fun createKeyFrom(
        masterKid: MasterKid,
        keyType: KeyType = KeyType.ASYMMETRIC,
        keyPurpose: KeyPurpose = KeyPurpose.SIGNATURE
    ): Kid

    fun deleteKeyFor(kid: Kid, keyPurpose: KeyPurpose, ttl: Duration = DELETE_NOW)

    fun signatureKeys(): Keys

    fun keyFor(kid: Kid, mfa: KeyPurpose): Key
}


open class AwsKeyRepository(
    private val clock: Clock,
    private val kidGenerator: () -> String,
    private val signatureTableName: String,
    private val mfaTableName: String,
    private val keyGenerator: KeyGenerator,
    private val dynamoDbClient: DynamoDbClient
) : KeyRepository {

    private val logger = LoggerFactory.getLogger(AwsKeyRepository::class.java)

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
                        "enabled" to true.asDynamoAttribute(),
                        "key_expiration_date_timestamp" to Duration.ofSeconds(0).toSeconds().asDynamoAttribute()

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

    override fun deleteKeyFor(kid: Kid, keyPurpose: KeyPurpose, ttl: Duration) {
        val key = keyFor(kid, keyPurpose)
        if (!key.enabled){
            throw KeyDeletionException("The key with kid: $kid is a rotated key.... it can not be deleted or rotated again." +
                    " Let's wait for the expiration time." +
                    " This key is not enabled to sign new token, only to verify already signed token before the rotation request")
        }
        val keys = validSignatureKeys()

        val noTtl = ttl.isZero
        val tableName = tableNameBasedOn(keyPurpose)

        if (noTtl) {
            if (keyPurpose == KeyPurpose.SIGNATURE && keys.size <= 1) {
                throw KeyDeletionException("at least one signature key is mandatory")
            }

            justDeleteKey(kid, tableName)
        } else {
            keyDeleteJodPlannedFor(kid, ttl, tableName)
        }
    }

    private fun validSignatureKeys() = Keys(signatureKeys().keys).validKeys().keys

    private fun justDeleteKey(kid: Kid, tableName: String) {
        dynamoDbClient.deleteItem(
            DeleteItemRequest.builder().tableName(tableName).key(
                mapOf(
                    "key_id" to kid.content().asDynamoAttribute(),
                )
            ).build()
        )
    }

    private fun keyDeleteJodPlannedFor(kid: Kid, ttl: Duration, tableName: String) {
        try {
            dynamoDbClient.updateItem(
                UpdateItemRequest.builder().tableName(tableName).key(
                    mapOf(
                        "key_id" to kid.content().asDynamoAttribute(),
                    )
                ).updateExpression("set enabled=:enabled, key_expiration_date_timestamp=:timestamp")
                    .expressionAttributeValues(
                        mapOf(
                            ":enabled" to false.asDynamoAttribute(),
                            ":timestamp" to ttl.expirationTimeStampInSecondFromNow(clock).asDynamoAttribute()
                        )
                    )
                    .conditionExpression("key_expiration_date_timestamp <> :timestamp")
                    .build()
            )
        } catch (e: ConditionalCheckFailedException) {
            logger.info("The key ${kid.content()} delete request is ignored... key is already disabled")
        }
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
        KeyPurpose.valueOf(it.valueAsStringFor("key_purpose")),
        it.valueAsLongFor("key_expiration_date_timestamp", 0)
    )

}