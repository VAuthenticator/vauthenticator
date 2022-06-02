package it.valeriovaudi.vauthenticator.security.login

import com.fasterxml.jackson.databind.ObjectMapper
import it.valeriovaudi.vauthenticator.extentions.oauth2ClientId
import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientAppId
import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientApplicationFeatures
import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientApplicationRepository
import it.valeriovaudi.vauthenticator.oauth2.clientapp.Scope
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.SessionAttributes
import javax.servlet.http.HttpSession


@Controller
@SessionAttributes("clientId", "features")
class LoginPageController(
        val clientApplicationRepository: ClientApplicationRepository,
        val objectMapper: ObjectMapper
) {

    @GetMapping("/login")
    fun loginPage(session: HttpSession, model: Model): String {
        val clientId = session.oauth2ClientId()

        val features = mutableMapOf(ClientApplicationFeatures.SIGNUP.value to false)
        clientId.ifPresent {
            model.addAttribute("clientId", it)
            clientApplicationRepository.findOne(ClientAppId(it))
                    .map {clientApp ->
                        features[ClientApplicationFeatures.SIGNUP.value] = clientApp.scopes.content.contains(Scope.SIGN_UP)
                    }
        }

        model.addAttribute("features", objectMapper.writeValueAsString(features))

        return "login"
    }


}