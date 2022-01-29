package it.valeriovaudi.vauthenticator.security.login

import it.valeriovaudi.vauthenticator.extentions.oauth2ClientId
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import javax.servlet.http.HttpSession


@Controller
class LoginPageController {

    @GetMapping("/login")
    fun loginPage(session: HttpSession, model: Model): String {
        val clientId = session.oauth2ClientId()
        clientId.ifPresent {
            val features = listOf("signUpLink=true")
            model.addAttribute("features", features)
        }

        return "login"
    }


}

