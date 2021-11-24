package it.valeriovaudi.vauthenticator.keypair

import software.amazon.awssdk.services.kms.KmsClient
import software.amazon.awssdk.services.kms.model.GenerateDataKeyPairRequest
import java.security.KeyFactory
import java.security.KeyPair
import java.security.PrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec


open class KmsKeyRepository(
        private val keyId: String,
        private val kmsClient: KmsClient
) : KeyRepository {

//    @Cacheable("keyPair")
    override fun getKeyPair(): KeyPair {
        val generateDataKeyPair = kmsClient.generateDataKeyPair(GenerateDataKeyPairRequest.builder()
                .keyPairSpec("RSA_2048")
                .keyId(keyId)
                .build())
        println(keyId)
        val kf: KeyFactory = KeyFactory.getInstance("RSA")

        val keySpecPKCS8 = PKCS8EncodedKeySpec(generateDataKeyPair.privateKeyPlaintext().asByteArray())
        val privKey: PrivateKey = kf.generatePrivate(keySpecPKCS8)

        val keySpecX509 = X509EncodedKeySpec(generateDataKeyPair.publicKey().asByteArray())
        val pubKey: RSAPublicKey = kf.generatePublic(keySpecX509) as RSAPublicKey

        println(generateDataKeyPair.privateKeyPlaintext())
        println(generateDataKeyPair.publicKey())

        return KeyPair(pubKey, privKey)
    }

}