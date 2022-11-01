package it.valeriovaudi.vauthenticator.oauth2.clientapp

object ClientAppFixture {
    fun aClientApp(clientAppId: ClientAppId,
                   password: Secret = Secret("secret"),
                   logoutUri: LogoutUri = LogoutUri("http://an_uri"),
                   authorities: Authorities = Authorities(listOf(Authority("AN_AUTHORITY")))
    ) = ClientApplication(
            clientAppId,
            password,
            Scopes.from(Scope.EMAIL, Scope.OPEN_ID, Scope.PROFILE),
            AuthorizedGrantTypes.from(AuthorizedGrantType.PASSWORD),
            CallbackUri("http://an_uri"),
            authorities,
            TokenTimeToLive(10),
            TokenTimeToLive(10),
            emptyMap(),
            AutoApprove.approve,
            PostLogoutRedirectUri("http://an_uri"),
            logoutUri)

    fun aClientAppId(): ClientAppId = ClientAppId("A_CLIENT_APP_ID")

}