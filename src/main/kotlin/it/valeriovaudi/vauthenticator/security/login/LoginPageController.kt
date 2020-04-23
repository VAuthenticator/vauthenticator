package it.valeriovaudi.vauthenticator.security.login

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping


@Controller
class LoginPageController {

    @GetMapping("/login")
    fun loginPage() = "login"

}

