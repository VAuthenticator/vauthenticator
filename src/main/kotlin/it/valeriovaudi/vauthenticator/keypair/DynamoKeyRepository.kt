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
import software.amazon.awssdk.services.kms.model.*
import java.security.KeyFactory
import java.security.KeyPair
import java.security.PrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*

open class DynamoKeyRepository(
    private val kidGenerator: () -> String,
    private val table: String,
    private val kmsKeyRepository: KmsKeyRepository,
    private val dynamoDbClient: DynamoDbClient
) : KeyRepository {

    override fun createKeyFrom(masterKid: MasterKid, keyType: KeyType): Kid {
        val dataKeyPair = keyPairFor(masterKid, keyType)
        val kidContent = kidGenerator.invoke()

        dynamoDbClient.putItem(
            PutItemRequest.builder()
                .tableName(table)
                .item(
                    mapOf(
                        "master_key_id" to masterKid.content().asDynamoAttribute(),
                        "key_id" to kidContent.asDynamoAttribute(),
                        "private_key" to dataKeyPair.privateKeyAsString().asDynamoAttribute(),
                        "public_key" to dataKeyPair.publicKeyAsString().asDynamoAttribute(),
                        "enabled" to true.asDynamoAttribute()
                    )
                )
                .build()
        )

        return Kid(kidContent)
    }

    private fun keyPairFor(masterKid: MasterKid, keyType: KeyType) =
        if (keyType == KeyType.ASYMMETRIC) {
            kmsKeyRepository.dataKeyPairFor(masterKid)
        } else {
            TODO()
        }

    override fun deleteKeyFor(masterKid: MasterKid, kid: Kid) {
        dynamoDbClient.deleteItem(
            DeleteItemRequest.builder()
                .tableName(table)
                .key(
                    mapOf(
                        "master_key_id" to masterKid.content().asDynamoAttribute(),
                        "key_id" to kid.content().asDynamoAttribute(),
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
                kmsKeyRepository.getKeyPairFor(
                    it.valueAsStringFor("private_key"),
                    it.valueAsStringFor("public_key")
                ),
                MasterKid(it.valueAsStringFor("master_key_id")),
                Kid(it.valueAsStringFor("key_id")),
                it.valueAsBoolFor("enabled")
            )
        }

}

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

    fun dataKeyPairFor(masterKid: MasterKid) =
        kmsClient.generateDataKeyPair(
            GenerateDataKeyPairRequest.builder()
                .keyId(masterKid.content())
                .keyPairSpec(DataKeyPairSpec.RSA_2048)
                .build()
        ).let {
            AwsKmsDataKey(
                privateKey = it.privateKeyCiphertextBlob().asByteArray(),
                publicKey = Optional.of(it.publicKey().asByteArray())
            )
        }

    fun dataKeyFor(masterKid: MasterKid): AwsKmsDataKey =
        kmsClient.generateDataKey(
            GenerateDataKeyRequest.builder()
                .keyId(masterKid.content())
                .keySpec(DataKeySpec.AES_256)
                .build()
        ).let {
            AwsKmsDataKey(
                privateKey = it.ciphertextBlob().asByteArray(),
                publicKey = Optional.empty<ByteArray>()
            )
        }


}

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

data class AwsKmsDataKey(val privateKey: ByteArray, val publicKey: Optional<ByteArray>) {

    fun privateKeyAsString() = encoder.encode(privateKey).decodeToString()
    fun publicKeyAsString() = publicKey.map { encoder.encode(it).decodeToString() }.orElseGet { "" }
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AwsKmsDataKey

        if (!privateKey.contentEquals(other.privateKey)) return false
        if (publicKey != other.publicKey) return false

        return true
    }

    override fun hashCode(): Int {
        var result = privateKey.contentHashCode()
        result = 31 * result + publicKey.hashCode()
        return result
    }
}