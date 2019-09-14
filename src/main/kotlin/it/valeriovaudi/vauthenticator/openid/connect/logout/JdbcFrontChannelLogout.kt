package it.valeriovaudi.vauthenticator.openid.connect.logout

import com.nimbusds.jose.JWSObject
import org.springframework.jdbc.core.JdbcTemplate

interface FrontChannelLogout {
    fun getFederatedLogoutUrls(clientId: String): List<String>
}

class JdbcFrontChannelLogout(private val jdbcTemplate: JdbcTemplate) : FrontChannelLogout {

    private val SELECT_QUERY = "SELECT logout_uris FROM oauth_client_details WHERE client_id=?"

    override fun getFederatedLogoutUrls(idTokenHint: String): List<String> {
        val audience = audFor(idTokenHint)
        val postLogoutRedirectUris = getPostLogoutRedirectUrisFor(audience)
        return postLogoutRedirectUris.split(",").toList()
    }

    private fun getPostLogoutRedirectUrisFor(audience: String) =
            jdbcTemplate.queryForObject(SELECT_QUERY, String::class.java, audience)

    private fun audFor(idTokenHint: String) =
            JWSObject.parse(idTokenHint).payload.toJSONObject().getAsString("aud") as String
}
