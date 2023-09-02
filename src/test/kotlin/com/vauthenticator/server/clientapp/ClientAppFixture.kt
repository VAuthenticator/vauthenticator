package com.vauthenticator.server.clientapp

import com.vauthenticator.server.oauth2.clientapp.*

const val A_CLIENT_APP_ID = "A_CLIENT_APP_ID"

object ClientAppFixture {
    fun aClientApp(
        clientAppId: ClientAppId,
        password: Secret = Secret("secret"),
        logoutUri: LogoutUri = LogoutUri("http://an_uri"),
        authorities: Authorities = Authorities(setOf(Authority("AN_AUTHORITY")))
    ) = ClientApplication(
        clientAppId,
        password,
        Scopes.from(Scope.EMAIL, Scope.OPEN_ID, Scope.PROFILE, Scope.RESET_PASSWORD),
        WithPkce.enabled,
        AuthorizedGrantTypes.from(AuthorizedGrantType.PASSWORD),
        CallbackUri("http://an_uri"),
        authorities,
        TokenTimeToLive(10),
        TokenTimeToLive(10),
        emptyMap(),
        AutoApprove.approve,
        PostLogoutRedirectUri("http://an_uri"),
        logoutUri
    )

    fun aClientAppId(): ClientAppId = ClientAppId(A_CLIENT_APP_ID)

}