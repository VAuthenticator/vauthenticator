package it.valeriovaudi.vauthenticator.openid.connect.logout

import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientAppId
import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientApplication.Companion.clientAppIdFrom
import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientApplicationRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

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
        val clientAppId = clientAppIdFrom(idTokenHint).content
        val clientAppLogoutUri = applicationRepository.findOne(ClientAppId(clientAppId)).map { it.logoutUri.content }.orElse("")
        val logoutUrisWithAuthServer = listOf("$authServerBaseUrl/logout", clientAppLogoutUri)
        logger.debug("logoutUrisWithAuthServer: $logoutUrisWithAuthServer")
        return logoutUrisWithAuthServer
    }

}
