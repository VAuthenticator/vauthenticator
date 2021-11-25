package it.valeriovaudi.vauthenticator.keypair

import it.valeriovaudi.vauthenticator.extentions.valueAsBoolFor
import it.valeriovaudi.vauthenticator.extentions.valueAsStringFor
import it.valeriovaudi.vauthenticator.keypair.KeyPairFactory.keyPairFor
import software.amazon.awssdk.core.SdkBytes.fromByteArray
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.ScanRequest
import software.amazon.awssdk.services.kms.KmsClient
import software.amazon.awssdk.services.kms.model.DecryptRequest
import java.security.KeyFactory
import java.security.KeyPair
import java.security.PrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec


open class DynamoKeyRepository(
        private val table: String,
        private val kmsKeyRepository: KmsKeyRepository,
        private val dynamoDbClient: DynamoDbClient
) : KeyRepository {

    override fun getKeyPair(): KeyPair {
        TODO()
    }

    override fun keys(): Keys {
        val keysOnDynamo = findAllFrom(table)
        val items = keysOnDynamo.items()
        val keysList = keysListFrom(items)
        return Keys(keysList)
    }

    private fun keysListFrom(items: MutableList<MutableMap<String, AttributeValue>>) =
            items
                    .map {
                        Key(
                                kmsKeyRepository.getKeyPairFor(it.valueAsStringFor("private_key_ciphertext_blob"), it.valueAsStringFor("public_key")),
                                it.valueAsStringFor("master_key_id"),
                                it.valueAsStringFor("key_id"),
                                it.valueAsBoolFor("enabled")
                        )
                    }

    private fun findAllFrom(table: String) = dynamoDbClient.scan(
            ScanRequest.builder()
                    .tableName(table)
                    .build()
    )


}

object KeyPairFactory {
    fun keyPairFor(privateKey: String, pubKey: String): KeyPair {
        val kf: KeyFactory = keyFactory()
        val privateKey: PrivateKey = privateKey(kf, privateKey)
        val pubKey: RSAPublicKey = rsaPublicKey(kf, pubKey)
        return KeyPair(pubKey, privateKey)
    }

    private fun keyFactory() = KeyFactory.getInstance("RSA")

    private fun rsaPublicKey(kf: KeyFactory, pubKey: String): RSAPublicKey {
        val keySpecX509 = X509EncodedKeySpec(pubKey.toByteArray())
        return kf.generatePublic(keySpecX509) as RSAPublicKey
    }

    private fun privateKey(kf: KeyFactory, privateKey: String): PrivateKey {
        val keySpecPKCS8 = PKCS8EncodedKeySpec(privateKey.toByteArray())
        return kf.generatePrivate(keySpecPKCS8)
    }
}

class KmsKeyRepository(
        private val kmsClient: KmsClient
) {

    fun getKeyPairFor(privateKey: String, pubKey: String): KeyPair {
        val generateDataKeyPair = kmsClient.decrypt(
                DecryptRequest.builder()
                        .ciphertextBlob(fromByteArray(privateKey.toByteArray()))
                        .build()
        )

        return keyPairFor(generateDataKeyPair.plaintext().toString(), pubKey)
    }


}