package it.valeriovaudi.vauthenticator.oauth2.clientapp

object ClientAppFixture {
    fun aClientApp(clientAppId: ClientAppId,
                   password: Secret = Secret("secret"),
                   federation: Federation = Federation("A_FEDERATION"),
                   logoutUri: LogoutUri = LogoutUri("http://an_uri")
    ) = ClientApplication(
            clientAppId,
            password,
            Scopes.from(Scope.EMAIL, Scope.OPEN_ID, Scope.PROFILE),
            AuthorizedGrantTypes.from(AuthorizedGrantType.PASSWORD),
            CallbackUri("http://an_uri"),
            Authorities(listOf(Authority("AN_AUTHORITY"))),
            TokenTimeToLive(10),
            TokenTimeToLive(10),
            emptyMap(),
            AutoApprove.approve,
            PostLogoutRedirectUri("http://an_uri"),
            logoutUri,
            federation,
                ResourceIds.from(ResourceId("oauth2-resource"))
    )
}