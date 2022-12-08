package it.valeriovaudi.vauthenticator.keys

import com.nimbusds.jose.jwk.RSAKey
import it.valeriovaudi.vauthenticator.extentions.encoder
import java.security.KeyPair
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
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

data class Key(val keyPair: KeyPair, val masterKid: MasterKid, val kid: Kid, val enabled: Boolean)

data class DataKey(val encryptedPrivateKey: ByteArray, val publicKey: Optional<ByteArray>) {

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

fun Keys.generateRsas() = this.keys
    .map {
        RSAKey.Builder(it.keyPair.public as RSAPublicKey)
            .privateKey(it.keyPair.private as RSAPrivateKey)
            .keyID(it.kid.content())
            .build()
    }