package com.vauthenticator.server.keys

import com.nimbusds.jose.jwk.RSAKey
import com.vauthenticator.server.extentions.decoder
import com.vauthenticator.server.extentions.encoder
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

class KeyDeletionException(message: String) : RuntimeException(message)

enum class KeyType { SYMMETRIC, ASYMMETRIC }
enum class KeyPurpose { SIGNATURE, MFA }
data class Keys(val keys: List<Key>) {

    fun generateRsas(keyDecrypter: KeyDecrypter) = this.keys
        .filter { it.type == KeyType.ASYMMETRIC }
        .map { mapOf("kid" to it.kid, "keyPair" to it.dataKey.keyPairWith(keyDecrypter)) }
        .map {
            val keyPair = it["keyPair"] as KeyPair
            val kid = it["kid"] as Kid
            RSAKey.Builder(keyPair.public as RSAPublicKey)
                .privateKey(keyPair.private as RSAPrivateKey)
                .keyID(kid.content())
                .build()
        }

}

data class Key(
    val dataKey: DataKey,
    val masterKid: MasterKid,
    val kid: Kid,
    val enabled: Boolean,
    val type: KeyType,
    val keyPurpose: KeyPurpose
)

data class DataKey(val encryptedPrivateKey: ByteArray, val publicKey: Optional<ByteArray>) {

    companion object {
        fun from(encryptedPrivateKey: String, pubKey: String): DataKey {
            return DataKey(decoder.decode(encryptedPrivateKey), Optional.of(decoder.decode(pubKey)))
        }
    }

    fun keyPairWith(keyDecrypter: KeyDecrypter): KeyPair {
        return KeyPairFactory.keyPairFor(
            keyDecrypter.decryptKey(this.encryptedPrivateKeyAsString()),
            this.publicKeyAsString()
        );
    }

    fun encryptedPrivateKeyAsString(): String = encoder.encode(encryptedPrivateKey).decodeToString()
    fun publicKeyAsString(): String = publicKey.map { encoder.encode(it).decodeToString() }.orElseGet { "" }
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DataKey

        if (!encryptedPrivateKey.contentEquals(other.encryptedPrivateKey)) return false

        if (!(publicKey.isEmpty && other.publicKey.isEmpty)) {
            if (!publicKey.get().contentEquals(other.publicKey.get())) return false
        } else {
            return false
        }

        return true
    }

    override fun hashCode(): Int {
        var result = encryptedPrivateKey.contentHashCode()
        result = 31 * result + publicKey.hashCode()
        return result
    }
}


