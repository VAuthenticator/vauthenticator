package it.valeriovaudi.vauthenticator.jwt

import org.springframework.security.oauth2.common.OAuth2AccessToken
import org.springframework.security.oauth2.provider.OAuth2Authentication

interface JwtEncoder {
    fun claimsFor(token: String): Map<String, Any?>
    fun encode(accessToken: OAuth2AccessToken, authentication: OAuth2Authentication): String
}