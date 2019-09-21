package it.valeriovaudi.vauthenticator.openid.connect.logout

import com.nimbusds.jose.JWSObject
import org.springframework.jdbc.core.JdbcTemplate
import java.sql.ResultSet

interface FrontChannelLogout {
    fun getFederatedLogoutUrls(clientId: String): List<String>
}

const val SELECT_QUERY = "SELECT logout_uris FROM oauth_client_details where client_id=?"

class JdbcFrontChannelLogout(private val jdbcTemplate: JdbcTemplate) : FrontChannelLogout {

    override fun getFederatedLogoutUrls(idTokenHint: String): List<String> {
        val audience = audFor(idTokenHint)
        return getPostLogoutRedirectUrisFor(audience)
    }

    private fun getPostLogoutRedirectUrisFor(audience: String) =
            jdbcTemplate.query(SELECT_QUERY, arrayOf(audience))
            { resultSet: ResultSet, _: Int -> resultSet.getString("logout_uris") }
                    .flatMap { it.split(",") }

    private fun audFor(idTokenHint: String) =
            JWSObject.parse(idTokenHint).payload.toJSONObject().getAsString("aud") as String
}
