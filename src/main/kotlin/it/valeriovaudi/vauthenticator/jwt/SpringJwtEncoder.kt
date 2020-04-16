package it.valeriovaudi.vauthenticator.jwt

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.jwt.JwtHelper
import org.springframework.security.jwt.crypto.sign.SignatureVerifier
import org.springframework.security.jwt.crypto.sign.Signer
import org.springframework.security.oauth2.common.OAuth2AccessToken
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException
import org.springframework.security.oauth2.common.util.JsonParser
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.security.oauth2.provider.token.AccessTokenConverter
import org.springframework.security.oauth2.provider.token.store.JwtClaimsSetVerifier

const val KID = "kid"

class SpringJwtEncoder(
        private val kid: String,
        private val tokenConverter: AccessTokenConverter,
        private val signer: Signer,
        private val jwtClaimsSetVerifier: JwtClaimsSetVerifier,
        private val objectMapper: JsonParser,
        private val verifier: SignatureVerifier) : JwtEncoder {

    private val logger : Logger = LoggerFactory.getLogger(SpringJwtEncoder::class.java)

    override fun claimsFor(token: String): Map<String, Any> {
        return try {
            val jwt = JwtHelper.decodeAndVerify(token, verifier)
            val claimsStr = jwt.claims
            val claims = objectMapper.parseMap(claimsStr)
            if (claims.containsKey(AccessTokenConverter.EXP) && claims[AccessTokenConverter.EXP] is Int) {
                val intValue = claims[AccessTokenConverter.EXP] as Int?
                claims[AccessTokenConverter.EXP] = intValue
            }
            this.jwtClaimsSetVerifier.verify(claims)
            claims
        } catch (e: Exception) {
            logger.error("Cannot convert access token to JSON", e)
            throw InvalidTokenException("Cannot convert access token to JSON", e)
        }
    }

    override fun encode(accessToken: OAuth2AccessToken, authentication: OAuth2Authentication): String {
        val content: String
        try {
            content = objectMapper.formatMap(tokenConverter.convertAccessToken(accessToken, authentication))
        } catch (e: Exception) {
            logger.error("Cannot convert access token to JSON", e)
            throw IllegalStateException("Cannot convert access token to JSON", e)
        }
        return JwtHelper.encode(content, signer, mapOf(KID to kid)).encoded
    }
}