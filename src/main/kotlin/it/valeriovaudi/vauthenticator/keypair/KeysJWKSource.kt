package it.valeriovaudi.vauthenticator.keypair

import com.nimbusds.jose.jwk.JWK
import com.nimbusds.jose.jwk.JWKSelector
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.SecurityContext

class KeysJWKSource(private val keyRepository: KeyRepository) : JWKSource<SecurityContext?> {
    override fun get(jwkSelector: JWKSelector, context: SecurityContext?): MutableList<JWK> {
        val rsaKey = keyRepository.keys().generateRsas()
        val jwkSet = JWKSet(rsaKey)
        return jwkSelector.select(jwkSet)
    }
}