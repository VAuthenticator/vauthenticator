package it.valeriovaudi.vauthenticator.keypair

import com.nimbusds.jose.jwk.RSAKey
import java.security.KeyPair
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

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

fun Keys.generateRsas() = this.keys
    .map {
        RSAKey.Builder(it.keyPair.public as RSAPublicKey)
            .privateKey(it.keyPair.private as RSAPrivateKey)
            .keyID(it.kid.content())
            .build()
    }