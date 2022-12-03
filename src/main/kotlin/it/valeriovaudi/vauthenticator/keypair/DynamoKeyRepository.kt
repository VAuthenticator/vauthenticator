package it.valeriovaudi.vauthenticator.keypair

import it.valeriovaudi.vauthenticator.extentions.*
import it.valeriovaudi.vauthenticator.keypair.KeyPairFactory.keyPairFor
import software.amazon.awssdk.core.SdkBytes.fromByteArray
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest
import software.amazon.awssdk.services.dynamodb.model.ScanRequest
import software.amazon.awssdk.services.kms.KmsClient
import software.amazon.awssdk.services.kms.model.DataKeyPairSpec
import software.amazon.awssdk.services.kms.model.DecryptRequest
import software.amazon.awssdk.services.kms.model.GenerateDataKeyPairRequest
import java.security.KeyFactory
import java.security.KeyPair
import java.security.PrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec

open class DynamoKeyRepository(
    private val kidGenerator: () -> String,
    private val table: String,
//    private val kmsKeyRepository: KmsKeyRepository,
    private val kmsClient: KmsClient,
    private val dynamoDbClient: DynamoDbClient
) : KeyRepository {

    override fun createKeyFrom(masterKid: MasterKid): Kid {
        val dataKeyPair = kmsClient.generateDataKeyPair(
            GenerateDataKeyPairRequest.builder()
                .keyId(masterKid)
                .keyPairSpec(DataKeyPairSpec.RSA_2048)
                .build()
        )
        val kid = kidGenerator.invoke()
        dynamoDbClient.putItem(
            PutItemRequest.builder()
                .tableName(table)
                .item(
                    mapOf(
                        "master_key_id" to masterKid.asDynamoAttribute(),
                        "key_id" to kid.asDynamoAttribute(),
                        "private_key_ciphertext_blob" to encoder.encode(
                            dataKeyPair.privateKeyCiphertextBlob().asByteArray()
                        ).decodeToString().asDynamoAttribute(),
                        "public_key" to encoder.encode(dataKeyPair.publicKey().asByteArray()).decodeToString()
                            .asDynamoAttribute(),
                        "enabled" to true.asDynamoAttribute()
                    )
                )
                .build()
        )

        return kid
    }

    override fun deleteKeyFor(masterKid: MasterKid, kid: Kid) {
        dynamoDbClient.deleteItem(
            DeleteItemRequest.builder()
                .tableName(table)
                .key(
                    mapOf(
                        "master_key_id" to masterKid.asDynamoAttribute(),
                        "key_id" to kid.asDynamoAttribute(),
                    )
                )
                .build()
        )
    }

    override fun keys(): Keys {
        val keysOnDynamo = findAllFrom(table)
        val items = keysOnDynamo.items()
        val keys = keysListFrom(items)
        return Keys(keys)
    }

    private fun findAllFrom(table: String) = dynamoDbClient.scan(
        ScanRequest.builder()
            .tableName(table)
            .build()
    )

    private fun keysListFrom(items: MutableList<MutableMap<String, AttributeValue>>) =
        items.map {
            Key(
                getKeyPairFor(
                    it.valueAsStringFor("private_key_ciphertext_blob"),
                    it.valueAsStringFor("public_key")
                ),
                it.valueAsStringFor("master_key_id"),
                it.valueAsStringFor("key_id"),
                it.valueAsBoolFor("enabled")
            )
        }

    private  fun getKeyPairFor(privateKey: String, pubKey: String): KeyPair {
        val generateDataKeyPair = kmsClient.decrypt(
            DecryptRequest.builder()
                .ciphertextBlob(fromByteArray(decoder.decode(privateKey)))
                .build()
        )

        return keyPairFor(encoder.encode(generateDataKeyPair.plaintext().asByteArray()).decodeToString(), pubKey)
    }
}
/*
class KmsKeyRepository(
    private val kmsClient: KmsClient
) {

    fun getKeyPairFor(privateKey: String, pubKey: String): KeyPair {
        val generateDataKeyPair = kmsClient.decrypt(
            DecryptRequest.builder()
                .ciphertextBlob(fromByteArray(decoder.decode(privateKey)))
                .build()
        )

        return keyPairFor(encoder.encode(generateDataKeyPair.plaintext().asByteArray()).decodeToString(), pubKey)
    }

}*/

object KeyPairFactory {
    fun keyPairFor(privateKey: String, pubKey: String): KeyPair {
        val kf: KeyFactory = keyFactory()
        val pubKey: RSAPublicKey = rsaPublicKey(kf, pubKey)
        val privateKey: PrivateKey = privateKey(kf, privateKey)
        return KeyPair(pubKey, privateKey)
    }

    private fun keyFactory() = KeyFactory.getInstance("RSA")

    private fun rsaPublicKey(kf: KeyFactory, pubKey: String): RSAPublicKey {
        val keySpecX509 = X509EncodedKeySpec(decoder.decode(pubKey))
        return kf.generatePublic(keySpecX509) as RSAPublicKey
    }

    private fun privateKey(kf: KeyFactory, privateKey: String): PrivateKey {
        val keySpecPKCS8 = PKCS8EncodedKeySpec(decoder.decode(privateKey))
        return kf.generatePrivate(keySpecPKCS8)
    }
}