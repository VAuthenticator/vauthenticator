package it.valeriovaudi.vauthenticator.openidconnect.idtoken

import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken
import org.springframework.security.oauth2.common.OAuth2AccessToken
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.security.oauth2.provider.token.TokenEnhancer

class IdTokenEnhancer : TokenEnhancer {

    override fun enhance(accessToken: OAuth2AccessToken, authentication: OAuth2Authentication): OAuth2AccessToken {
        var defaultAccessToken = accessToken as DefaultOAuth2AccessToken

        val additionalInformation = defaultAccessToken.additionalInformation
        additionalInformation["id_token"] = ""

        defaultAccessToken.additionalInformation = additionalInformation

        return defaultAccessToken
    }

}