package it.valeriovaudi.vauthenticator.keys

import it.valeriovaudi.vauthenticator.extentions.*
import software.amazon.awssdk.core.SdkBytes.fromByteArray
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.*
import software.amazon.awssdk.services.kms.KmsClient
import software.amazon.awssdk.services.kms.model.*
import java.util.*

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
                )
                .build()
        )
    }

    private fun keyPairFor(masterKid: MasterKid, keyType: KeyType) =
        if (keyType == KeyType.ASYMMETRIC) {
            keyGenerator.dataKeyPairFor(masterKid)
        } else {
            keyGenerator.dataKeyFor(masterKid)
        }

    override fun deleteKeyFor(kid: Kid, keyPurpose: KeyPurpose) {
        val tableName = tableNameBasedOn(keyPurpose)
        dynamoDbClient.deleteItem(
            DeleteItemRequest.builder()
                .tableName(tableName)
                .key(
                    mapOf(
                        "key_id" to kid.content().asDynamoAttribute(),
                    )
                )
                .build()
        )
    }

    private fun tableNameBasedOn(keyPurpose: KeyPurpose) = when (keyPurpose) {
        KeyPurpose.MFA -> mfaTableName
        KeyPurpose.SIGNATURE -> signatureTableName
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
            GetItemRequest.builder()
                .tableName(tableName)
                .key(
                    mapOf(
                        "key_id" to kid.content().asDynamoAttribute()
                    )
                )
                .build()
        )
            .item()
            .let { keyFromDynamoFor(it) }
    }

    private fun findAllFrom(table: String) = dynamoDbClient.scan(
        ScanRequest.builder()
            .tableName(table)
            .build()
    )

    private fun keysListFrom(items: MutableList<MutableMap<String, AttributeValue>>) =
        items.map { keyFromDynamoFor(it) }

    private fun keyFromDynamoFor(it: MutableMap<String, AttributeValue>) =
        Key(
            DataKey.from(
                it.valueAsStringFor("encrypted_private_key"),
                it.valueAsStringFor("public_key")
            ),
            MasterKid(it.valueAsStringFor("master_key_id")),
            Kid(it.valueAsStringFor("key_id")),
            it.valueAsBoolFor("enabled"),
            KeyType.valueOf(it.valueAsStringFor("key_type")),
            KeyPurpose.valueOf(it.valueAsStringFor("key_purpose"))
        )

}

class KmsKeyRepository(
    private val kmsClient: KmsClient
) : KeyDecrypter, KeyGenerator {

    override fun decryptKeyAsByteArray(privateKey: String): ByteArray = kmsClient.decrypt(
        DecryptRequest.builder()
            .ciphertextBlob(fromByteArray(decoder.decode(privateKey)))
            .build()
    )
        .plaintext().asByteArray()


    override fun decryptKey(privateKey: String): String =
        decryptKeyAsByteArray(privateKey).let {
            encoder.encode(it).decodeToString()
        }

    override fun dataKeyPairFor(masterKid: MasterKid) =
        kmsClient.generateDataKeyPair(
            GenerateDataKeyPairRequest.builder()
                .keyId(masterKid.content())
                .keyPairSpec(DataKeyPairSpec.RSA_2048)
                .build()
        ).let {
            DataKey(
                encryptedPrivateKey = it.privateKeyCiphertextBlob().asByteArray(),
                publicKey = Optional.of(it.publicKey().asByteArray())
            )
        }

    override fun dataKeyFor(masterKid: MasterKid): DataKey =
        kmsClient.generateDataKey(
            GenerateDataKeyRequest.builder()
                .keyId(masterKid.content())
                .keySpec(DataKeySpec.AES_256)
                .build()
        ).let {
            DataKey(
                encryptedPrivateKey = it.ciphertextBlob().asByteArray(),
                publicKey = Optional.empty<ByteArray>()
            )
        }

}