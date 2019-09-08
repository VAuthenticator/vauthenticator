package it.valeriovaudi.vauthenticator.openid.connect.discovery

data class OpenIdConnectDiscovery(var issuer: String = "",
                                  var authorization_endpoint: String = "",
                                  var token_endpoint: String = "",
                                  var jwks_uri: String = "",
                                  var userinfo_endpoint: String = "",
                                  var response_types_supported: List<String> = emptyList(),
                                  var grant_types_supported: List<String> = emptyList(),
                                  var subject_types_supported: List<String> = emptyList(),
                                  var id_token_signing_alg_values_supported: List<String> = emptyList(),
                                  var scopes_supported: List<String> = emptyList(),
                                  var token_endpoint_auth_methods_supported: List<String> = emptyList(),
                                  var claims_supported: List<String> = emptyList(),
                                  var code_challenge_methods_supported: List<String> = emptyList()
) {
    companion object {
        fun newOpenIdConnectDiscovery(issuer: String) =
                OpenIdConnectDiscovery(issuer = issuer,
                        authorization_endpoint = "$issuer/oauth/authorize",
                        token_endpoint = "$issuer/oauth/token",
                        jwks_uri = "$issuer/.well-known/jwks.json",
                        userinfo_endpoint = "$issuer/user-info",
                        response_types_supported = listOf("code"),
                        grant_types_supported = listOf("authorization_code"),
                        subject_types_supported = listOf("public"),
                        id_token_signing_alg_values_supported = listOf("RS256"),
                        scopes_supported = listOf("openid", "email", "profile"),
                        token_endpoint_auth_methods_supported = listOf("client_secret_post", "client_secret_basic"),
                        claims_supported = listOf("email", "email_verified", "name", "given_name", "family_name", "middle_name", "iss", "sub", "aud", "exp", "iat", "auth_time"),
                        code_challenge_methods_supported = listOf("plain")
                )
    }
}