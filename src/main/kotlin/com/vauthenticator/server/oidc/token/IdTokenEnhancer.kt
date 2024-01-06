package com.vauthenticator.server.oidc.token

import com.vauthenticator.server.keys.KeyRepository
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer

class IdTokenEnhancer(private val keyRepository: KeyRepository) : OAuth2TokenCustomizer<JwtEncodingContext> {
    override fun customize(context: JwtEncodingContext) {
        val tokenType = context.tokenType.value

        if ("id_token" == tokenType && !context.authorizationGrantType.equals(AuthorizationGrantType.CLIENT_CREDENTIALS)) {
            val signatureKey = keyRepository.signatureKeys().peekOnAtRandom()
            val attributes = context.authorization!!.attributes
            val principle = attributes["java.security.Principal"] as Authentication
            context.claims.claim("email", principle.name)

            context.jwsHeader.keyId(signatureKey.kid.content())
        }
    }
}