package com.vauthenticator.server.support

import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.JwsHeader
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext

object JwtEncodingContextFixture {

    val newContext: JwtEncodingContext = JwtEncodingContext.with(
        JwsHeader.with(MacAlgorithm.HS256),
        JwtClaimsSet.builder()
    )
        .tokenType(OAuth2TokenType.ACCESS_TOKEN)
        .registeredClient(
            RegisteredClient.withId("client_id")
                .clientId("client_id")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("http://localhost/calback")
                .build()
        )
        .build()
}