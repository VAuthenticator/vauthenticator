package it.valeriovaudi.vauthenticator.openidconnect.discovery

data class OpenIdConnectDiscovery(var issuer: String = "",
                                  var authorization_endpoint: String = "",
                                  var token_endpoint: String = "",
                                  var jwks_uri: String = "",
                                  var userinfo_endpoint: String = "",
                                  var response_types_supported: List<String> = emptyList(),
                                  var subject_types_supported: List<String> = emptyList(),
                                  var id_token_signing_alg_values_supported: List<String> = emptyList()) {
    companion object {
        fun newOpenIdConnectDiscovery(issuer: String) =
                OpenIdConnectDiscovery(issuer,
                        "$issuer/oauth/authorize",
                        "$issuer/oauth/token",
                        "$issuer/.well-known/jwks.json",
                        "$issuer/user-info",
                        listOf("code", "code id_token", "id_token", "token id_token"),
                        listOf("public", "pairwise"),
                        listOf("RS256"))


    }
}