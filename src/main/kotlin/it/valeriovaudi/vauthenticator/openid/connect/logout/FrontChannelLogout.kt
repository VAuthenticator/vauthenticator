package it.valeriovaudi.vauthenticator.openid.connect.logout

import com.nimbusds.jose.JWSObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import java.sql.ResultSet

interface FrontChannelLogout {
    fun getFederatedLogoutUrls(clientId: String): List<String>
}

@Controller
class FrontChannelLogoutController(private val frontChannelLogout: FrontChannelLogout) {

    @GetMapping("/oidc/logout")
    fun frontChannelGlobalLogout(model: Model,
                                 @RequestParam("post_logout_redirect_uri") postLogoutRedirectUri: String,
                                 @RequestParam("id_token_hint") idTokenHint: String): String {
        val federatedServers = frontChannelLogout.getFederatedLogoutUrls(idTokenHint)

        model.addAttribute("federatedServers", federatedServers)
        return "logout/oidc/global_logout"
    }

}

const val SELECT_QUERY = "SELECT logout_uris FROM oauth_client_details where federation = (SELECT federation FROM oauth_client_details where client_id=?)"

class JdbcFrontChannelLogout(private val authServerBaseUrl: String, private val jdbcTemplate: JdbcTemplate) : FrontChannelLogout {

    val logger : Logger = LoggerFactory.getLogger(JdbcFrontChannelLogout::class.java)

    override fun getFederatedLogoutUrls(idTokenHint: String): List<String> {
        val audience = audFor(idTokenHint)
        val logoutUris = getPostLogoutRedirectUrisFor(audience).toTypedArray()
        val logoutUrisWithAuthServer = listOf("$authServerBaseUrl/logout", *logoutUris)
        logger.debug("logoutUrisWithAuthServer: $logoutUrisWithAuthServer")
        return logoutUrisWithAuthServer
    }

    private fun getPostLogoutRedirectUrisFor(audience: String) =
            jdbcTemplate.query(SELECT_QUERY, arrayOf(audience))
            { resultSet: ResultSet, _: Int -> resultSet.getString("logout_uris") }
                    .flatMap { it.split(",") }

    private fun audFor(idTokenHint: String) =
            JWSObject.parse(idTokenHint).payload.toJSONObject().getAsString("aud") as String
}
