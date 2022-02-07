package it.valeriovaudi.vauthenticator.security.login

import it.valeriovaudi.vauthenticator.extentions.oauth2ClientId
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.SessionAttributes
import javax.servlet.http.HttpSession


@Controller
@SessionAttributes("features")
class LoginPageController {

    @GetMapping("/login")
    fun loginPage(session: HttpSession, model: Model): String {
        val clientId = session.oauth2ClientId()
        var features = listOf(
                "signUpLink=%s",
                "socialLogin=%s",
        )
        println(features)

        clientId
                .map { features = features.map { String.format(it, false) } }
                .orElseGet { features = features.map { String.format(it, false) } }

        model.addAttribute("features", features)

        return "login"
    }


}

