package it.valeriovaudi.vauthenticator.oauth2.clientapp

object ClientAppFixture {
    fun aClientApp(clientAppId: ClientAppId,
                   password: Secret = EmptySecret,
                   federation: Federation = Federation("A_FEDERATION")
    ) = ClientApplication(
            clientAppId,
            password,
            Scopes.from(Scope.OPEN_ID, Scope.PROFILE, Scope.EMAIL),
            AuthorizedGrantTypes.from(AuthorizedGrantType.PASSWORD),
            CallbackUri("http://an_uri"),
            Authorities(listOf(Authority("AN_AUTHORITY"))),
            TokenTimeToLive(10),
            TokenTimeToLive(10),
            emptyMap(),
            AutoApprove.approve,
            PostLogoutRedirectUri("http://an_uri"),
            LogoutUri("http://an_uri"),
            federation,
            ResourceIds.from(ResourceId("oauth2-resource"))
    )
}