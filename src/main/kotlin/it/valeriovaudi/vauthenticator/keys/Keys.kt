package it.valeriovaudi.vauthenticator.keys

import com.nimbusds.jose.jwk.RSAKey
import it.valeriovaudi.vauthenticator.extentions.decoder
import it.valeriovaudi.vauthenticator.extentions.encoder
import java.security.KeyFactory
import java.security.KeyPair
import java.security.PrivateKey
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*

@JvmInline
value class Kid(private val content: String) {
    fun content() = content
}

@JvmInline
value class MasterKid(private val content: String) {
    fun content() = content
}


enum class KeyType { SYMMETRIC, ASYMMETRIC }
data class Keys(val keys: List<Key>)

data class Key(val dataKey: DataKey, val masterKid: MasterKid, val kid: Kid, val enabled: Boolean)

data class DataKey(val encryptedPrivateKey: ByteArray, val publicKey: Optional<ByteArray>) {

    companion object {
        fun from(encryptedPrivateKey: String, pubKey: String) =
            DataKey(decoder.decode(encryptedPrivateKey), Optional.of(pubKey.toByteArray()))
    }

    fun keyPairWith(keyDecrypter: KeyDecrypter): KeyPair {
        return KeyPairFactory.keyPairFor(keyDecrypter.decryptKey(this.privateKeyAsString()), this.publicKeyAsString());
    }

    fun privateKeyAsString(): String = encoder.encode(encryptedPrivateKey).decodeToString()
    fun publicKeyAsString(): String = publicKey.map { encoder.encode(it).decodeToString() }.orElseGet { "" }
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DataKey

        if (!encryptedPrivateKey.contentEquals(other.encryptedPrivateKey)) return false
        if (publicKey != other.publicKey) return false

        return true
    }

    override fun hashCode(): Int {
        var result = encryptedPrivateKey.contentHashCode()
        result = 31 * result + publicKey.hashCode()
        return result
    }
}


fun Keys.generateRsas(keyDecrypter: KeyDecrypter) = this.keys
    .map { mapOf("kid" to it.kid, "keyPair" to it.dataKey.keyPairWith(keyDecrypter)) }
    .map {
        val keyPair = it["keyPair"] as KeyPair
        val kid = it["kid"] as Kid
        RSAKey.Builder(keyPair.public as RSAPublicKey)
            .privateKey(keyPair.private as RSAPrivateKey)
            .keyID(kid.content())
            .build()
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