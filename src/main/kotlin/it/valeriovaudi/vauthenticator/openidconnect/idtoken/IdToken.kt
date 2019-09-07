package it.valeriovaudi.vauthenticator.openidconnect.idtoken

import com.nimbusds.jose.*
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jwt.JWTClaimsSet
import it.valeriovaudi.vauthenticator.time.Clock
import org.slf4j.LoggerFactory
import org.springframework.security.oauth2.provider.OAuth2Authentication
import java.security.KeyPair

val logger = LoggerFactory.getLogger(IdToken::class.java.name)

data class IdToken(val email: String,
                   val iss: String,
                   val sub: String,
                   val aud: String,
                   val nonce: String?,
                   val exp: Long,
                   val iat: Long,
                   val auth_time: Long) {


    companion object {
        fun createIdToken(iss: String,
                          sub: String,
                          authentication: OAuth2Authentication,
                          clock: Clock) =
                clock.nowInSeconds()
                        .let { now ->
                            IdToken(email = authentication.name,
                                    iss = iss,
                                    sub = sub,
                                    aud = authentication.oAuth2Request.clientId,
                                    nonce = nonce(authentication),
                                    exp = now * 20,
                                    iat = now,
                                    auth_time = now)
                        }

        private fun nonce(authentication: OAuth2Authentication) =
                authentication.oAuth2Request.extensions["nonce"] as String?
    }

    fun idTokenAsJwtSignedFor(key: KeyPair): String {
        val jwsObject = JWSObject(header(), payload())
        try {
            jwsObject.sign(RSASSASigner(key.private))
        } catch (e: JOSEException) {
            logger.error(e.message, e)
        }

        return jwsObject.serialize()
    }

    private fun header(): JWSHeader {
        return JWSHeader(JWSAlgorithm.RS256)
    }

    private fun payload(): Payload {
        val jwtClaimsBuilder = JWTClaimsSet.Builder()
                .claim("email", this.email)
                .claim("iss", this.iss)
                .claim("sub", this.sub)
                .claim("aud", this.aud)
                .claim("exp", this.exp)
                .claim("iat", this.iat)
                .claim("auth_time", this.auth_time)

        applyNonce(nonce, jwtClaimsBuilder)

        val jwtClaims = jwtClaimsBuilder.build()
        return Payload(jwtClaims.toJSONObject())
    }

    private fun applyNonce(nonce: String?, jwtClaimsSetBuilder: JWTClaimsSet.Builder) {
        if ((nonce ?: "").isNotBlank()) {
            jwtClaimsSetBuilder.claim("nonce", this.nonce)
        }

    }
}