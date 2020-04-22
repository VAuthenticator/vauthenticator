package it.valeriovaudi.vauthenticator.web.controller

import it.valeriovaudi.vauthenticator.openid.connect.logout.FrontChannelLogout
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam


@Controller
class LoginPageController {

    @GetMapping("/login")
    fun loginPage() = "login"

}

@Controller
class FrontChannelLogoutController(private val frontChannelLogout: FrontChannelLogout) {

    @GetMapping("/oidc/logout")
    fun forntChannelGlobalLogout(model: Model,
                                 @RequestParam("post_logout_redirect_uri") postLogoutRedirectUri: String,
                                 @RequestParam("id_token_hint") idTokenHint: String): String {
        val federatedServers = frontChannelLogout.getFederatedLogoutUrls(idTokenHint)

        model.addAttribute("federatedServers", federatedServers)
        return "logout/oidc/global_logout"
    }

}