package it.valeriovaudi.vauthenticator.openid.connect.logout

import com.nimbusds.jose.JWSObject
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.query
import java.sql.ResultSet

interface FrontChannelLogout {
    fun getFederatedLogoutUrls(clientId: String): List<String>
}

class JdbcFrontChannelLogout(private val jdbcTemplate: JdbcTemplate) : FrontChannelLogout {

    private val SELECT_QUERY = "SELECT logout_uris FROM oauth_client_details"

    override fun getFederatedLogoutUrls(idTokenHint: String): List<String> {
        val audience = audFor(idTokenHint)
        val postLogoutRedirectUris = getPostLogoutRedirectUrisFor(audience)
        return postLogoutRedirectUris.flatMap { it.split(",") }
    }

    private fun getPostLogoutRedirectUrisFor(audience: String) =
            jdbcTemplate.query(SELECT_QUERY) { resultSet: ResultSet, _: Int -> resultSet.getString("logout_uris") }

    private fun audFor(idTokenHint: String) =
            JWSObject.parse(idTokenHint).payload.toJSONObject().getAsString("aud") as String
}
