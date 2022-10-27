package it.valeriovaudi.vauthenticator.keypair

import com.nimbusds.jose.jwk.RSAKey
import java.security.KeyPair
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

typealias Kid = String
typealias MasterKid = String

data class Keys(val keys: List<Key>)

data class Key(val keyPair: KeyPair, val masterKid: MasterKid, val kid: Kid, val enabled: Boolean)

fun Keys.generateRsas() = this.keys
    .map {
        RSAKey.Builder(it.keyPair.public as RSAPublicKey)
            .privateKey(it.keyPair.private as RSAPrivateKey)
            .keyID(it.kid)
            .build()
    }