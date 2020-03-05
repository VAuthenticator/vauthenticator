package it.valeriovaudi.vauthenticator.oauth2.token

import it.valeriovaudi.vauthenticator.jwt.JwtEncoder
import org.springframework.security.jwt.JwtHelper
import org.springframework.security.oauth2.common.*
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter
import java.util.*
import kotlin.collections.LinkedHashMap

class VAuthenticatorJwtAccessTokenConverter(private val jwtEncoder: JwtEncoder) : JwtAccessTokenConverter() { //TokenEnhancer, AccessTokenConverter

    override fun enhance(accessToken: OAuth2AccessToken, authentication: OAuth2Authentication): OAuth2AccessToken {
        val result = DefaultOAuth2AccessToken(accessToken)
        val info: MutableMap<String, Any?> = LinkedHashMap(accessToken.additionalInformation)
        var tokenId: String? = result.value
        if (!info.containsKey(TOKEN_ID)) {
            info[TOKEN_ID] = tokenId
        } else {
            tokenId = info.get(TOKEN_ID) as String?
        }
        result.additionalInformation = info
        result.value = jwtEncoder.encode(result, authentication)

        val refreshToken: OAuth2RefreshToken? = result.refreshToken
        if (refreshToken != null) {
            val encodedRefreshToken = DefaultOAuth2AccessToken(accessToken)
            encodedRefreshToken.value = refreshToken.value
            // Refresh tokens do not expire unless explicitly of the right type
            encodedRefreshToken.expiration = null
            try {
                val claims: Map<String, Any?> = jwtEncoder.claimsFor(refreshToken.value)

                if (claims.containsKey(TOKEN_ID)) {
                    encodedRefreshToken.value = claims.get(TOKEN_ID).toString()
                }
            } catch (e: RuntimeException) {
                e.printStackTrace()
            }
            val refreshTokenInfo: MutableMap<String, Any?> = LinkedHashMap(
                    accessToken.additionalInformation)
            refreshTokenInfo.put(TOKEN_ID, encodedRefreshToken.value)
            refreshTokenInfo.put(ACCESS_TOKEN_ID, tokenId)
            encodedRefreshToken.additionalInformation = refreshTokenInfo
            val encode = jwtEncoder.encode(encodedRefreshToken, authentication)

            var token: DefaultOAuth2RefreshToken? = DefaultOAuth2RefreshToken(encode)
            if (refreshToken is ExpiringOAuth2RefreshToken) {
                val expiration: Date = refreshToken.expiration
                encodedRefreshToken.expiration = expiration
                token = DefaultExpiringOAuth2RefreshToken(encode, expiration)
            }
            result.refreshToken = token
        }
        return result
    }

    override fun decode(token: String): Map<String, Any> {
        return jwtEncoder.claimsFor(token)
    }

    override fun encode(accessToken: OAuth2AccessToken, authentication: OAuth2Authentication): String {
        return jwtEncoder.encode(accessToken, authentication)
    }
}