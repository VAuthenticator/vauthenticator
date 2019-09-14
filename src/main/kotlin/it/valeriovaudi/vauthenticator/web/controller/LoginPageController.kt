package it.valeriovaudi.vauthenticator.web.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping


@Controller
class LoginPageController {

    @GetMapping("/singin")
    fun loginPage() = "login/singin"


    @GetMapping("/logout/oidc/global_logout")
    fun forntChannelGlobalLogout() = "/logout/oidc/global_logout"


}
