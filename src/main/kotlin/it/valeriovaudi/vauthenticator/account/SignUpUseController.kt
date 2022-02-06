package it.valeriovaudi.vauthenticator.account

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.SessionAttributes
import javax.servlet.http.HttpSession

@Controller
@SessionAttributes("features")
class SignUpController {

    @GetMapping("/sign-up")
    fun view(session: HttpSession, @ModelAttribute("features") features: List<String>): String {
        return "signup"
    }
}