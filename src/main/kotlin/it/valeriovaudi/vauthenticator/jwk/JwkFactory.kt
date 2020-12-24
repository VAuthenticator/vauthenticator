package it.valeriovaudi.vauthenticator.jwk

import com.nimbusds.jose.jwk.KeyUse
import com.nimbusds.jose.jwk.RSAKey
import java.security.KeyPair
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

open class JwkFactory {
    open fun createJwks(keyPair: KeyPair, alias: String): Map<String, Any> =
            RSAKey.Builder(keyPair.getPublic() as RSAPublicKey)
                .privateKey(keyPair.getPrivate() as RSAPrivateKey)
                .keyUse(KeyUse.SIGNATURE)
                .keyID(alias)
                .build()
                .toJSONObject()
}