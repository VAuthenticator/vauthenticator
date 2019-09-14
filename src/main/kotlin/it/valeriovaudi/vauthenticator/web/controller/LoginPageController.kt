package it.valeriovaudi.vauthenticator.web.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping


@Controller
class LoginPageController {

    @GetMapping("/singin")
    fun loginPage() = "login/singin"

    @GetMapping("/oidc/logout")
    fun forntChannelGlobalLogout(model: Model): String {
        var federatedServers =
                listOf("http://localhost:8080/family-budget/logout",
                        "http://localhost:8080/account/logout",
                        "http://localhost:8080/vauthenticator/logout"
                )

        model.addAttribute("federatedServers", federatedServers)
        return "/logout/oidc/global_logout"
    }

}