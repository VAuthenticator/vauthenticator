package it.valeriovaudi.vauthenticator.openidconnect.idtoken

import com.nimbusds.jose.*
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jwt.JWTClaimsSet
import it.valeriovaudi.vauthenticator.time.Clock
import org.slf4j.LoggerFactory
import org.springframework.security.oauth2.provider.OAuth2Authentication
import java.security.KeyPair

val logger = LoggerFactory.getLogger(IdToken::class.java.name)

data class IdToken(val userName: String,
                   val iss: String,
                   val sub: String,
                   val aud: String,
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
                            IdToken(authentication.name,
                                    iss, sub, authentication.oAuth2Request.clientId,
                                    now * 20,
                                    now,
                                    now)
                        }

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

    private fun payload() = JWTClaimsSet.Builder()
            .claim("email", this.userName)
            .claim("iss", this.iss)
            .claim("sub", this.sub)
            .claim("aud", this.aud)
            .claim("exp", this.exp)
            .claim("iat", this.iat)
            .claim("auth_time", this.auth_time)
            .build()
            .let { Payload(it.toJSONObject()) }

}