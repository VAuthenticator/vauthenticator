package it.valeriovaudi.vauthenticator.openid.connect.logout

import com.nimbusds.jose.JWSObject
import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientApplicationRepository
import it.valeriovaudi.vauthenticator.oauth2.clientapp.Federation
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import java.util.*

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

class JdbcFrontChannelLogout(
    private val authServerBaseUrl: String,
    private val applicationRepository: ClientApplicationRepository
) : FrontChannelLogout {

    private val logger: Logger = LoggerFactory.getLogger(JdbcFrontChannelLogout::class.java)

    override fun getFederatedLogoutUrls(idTokenHint: String): List<String> {
        val federation = federationFor(idTokenHint)
        val federatedLogoutUri = applicationRepository.findLogoutUriByFederation(Federation(federation)).map { it.content }

        val logoutUrisWithAuthServer = listOf("$authServerBaseUrl/logout", *federatedLogoutUri.toTypedArray())
        logger.debug("logoutUrisWithAuthServer: $logoutUrisWithAuthServer")
        return logoutUrisWithAuthServer
    }

    private fun federationFor(idTokenHint: String) =
            JWSObject.parse(idTokenHint).payload.toJSONObject().get("federation") as String
}
