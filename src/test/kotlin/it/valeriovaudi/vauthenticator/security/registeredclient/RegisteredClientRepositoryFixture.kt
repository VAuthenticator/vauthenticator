package it.valeriovaudi.vauthenticator.security.registeredclient

import it.valeriovaudi.vauthenticator.oauth2.clientapp.*
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.ClientAuthenticationMethod
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.config.TokenSettings
import java.time.Duration
import java.util.*

object RegisteredClientRepositoryFixture {

    fun aRegisteredClient() = RegisteredClient.withId("A_CLIENT_APP_ID")
        .clientId("A_CLIENT_APP_ID")
        .clientSecret("A_SECRET")
        .clientAuthenticationMethod(ClientAuthenticationMethod.BASIC)
        .clientAuthenticationMethod(ClientAuthenticationMethod.POST)
        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
        .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
        .scope("A_SCOPE")
        .scope("ANOTHER_SCOPE")
        .redirectUri("http://a_call_back")
        .tokenSettings { tokenSettings: TokenSettings ->
            tokenSettings.accessTokenTimeToLive(Duration.ofSeconds(100))
            tokenSettings.refreshTokenTimeToLive(Duration.ofSeconds(200))
            tokenSettings.reuseRefreshTokens(true)
        }
        .build()

    fun aClientApplication() = Optional.ofNullable(
        ClientApplication(
            clientAppId = ClientAppId("A_CLIENT_APP_ID"),
            secret = Secret("A_SECRET"),
            scopes = Scopes(setOf(Scope("A_SCOPE"), Scope("ANOTHER_SCOPE"))),
            authorizedGrantTypes = AuthorizedGrantTypes(
                listOf(
                    AuthorizedGrantType.AUTHORIZATION_CODE,
                    AuthorizedGrantType.REFRESH_TOKEN
                )
            ),
            webServerRedirectUri = CallbackUri("http://a_call_back"),
            authorities = Authorities(listOf(Authority("AN_AUTHORITY"), Authority("ANOTHER_AUTHORITY"))),
            accessTokenValidity = TokenTimeToLive(100),
            refreshTokenValidity = TokenTimeToLive(200),
            additionalInformation = emptyMap(),
            autoApprove = AutoApprove.approve,
            postLogoutRedirectUri = PostLogoutRedirectUri("http://post_logout_redirect_uri"),
            logoutUri = LogoutUri("http://logout_uri"),
            federation = Federation("federation"),
            resourceIds = ResourceIds(listOf(ResourceId("resource_id")))
        )
    )
}