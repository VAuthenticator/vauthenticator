package it.valeriovaudi.vauthenticator.oauth2.token

import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.server.authorization.JwtEncodingContext
import org.springframework.security.oauth2.server.authorization.OAuth2TokenCustomizer
import java.util.stream.Collectors

class OAuth2TokenEnhancer : OAuth2TokenCustomizer<JwtEncodingContext> {
    override fun customize(context: JwtEncodingContext) {
        val tokenType = context.tokenType.value
        if ("access_token" == tokenType && !context.authorizationGrantType.equals(AuthorizationGrantType.CLIENT_CREDENTIALS)) {
            val attributes =
                    context.authorization!!.attributes
            val principal =
                    attributes["java.security.Principal"] as Authentication

            context.claims.claim("user_name", principal.name)
            context.claims.claim("authorities", principal.authorities
                    .stream()
                    .map { obj: GrantedAuthority -> obj.authority }
                    .collect(Collectors.toList()))
        }    }
}