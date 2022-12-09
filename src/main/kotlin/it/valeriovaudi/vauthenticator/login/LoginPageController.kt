package it.valeriovaudi.vauthenticator.login

import com.fasterxml.jackson.databind.ObjectMapper
import it.valeriovaudi.vauthenticator.extentions.oauth2ClientId
import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientAppId
import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientApplicationFeatures
import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientApplicationRepository
import it.valeriovaudi.vauthenticator.oauth2.clientapp.Scope
import jakarta.servlet.http.HttpSession
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.SessionAttributes


@Controller
@SessionAttributes("clientId", "features")
class LoginPageController(
    val clientApplicationRepository: ClientApplicationRepository,
    val objectMapper: ObjectMapper
) {
    val logger: Logger = LoggerFactory.getLogger(LoginPageController::class.java)

    @GetMapping("/login")
    fun loginPage(session: HttpSession, model: Model): String {
        val clientId = session.oauth2ClientId()

        val features = defaultFeature()
        clientId.ifPresent {
            model.addAttribute("clientId", it)
            clientApplicationRepository.findOne(ClientAppId(it))
                .map { clientApp ->
                    logger.debug("clientApp.scopes.content: ${clientApp.scopes.content}")
                    features[ClientApplicationFeatures.SIGNUP.value] = clientApp.scopes.content.contains(Scope.SIGN_UP)
                    features[ClientApplicationFeatures.RESET_PASSWORD.value] =
                        clientApp.scopes.content.contains(Scope.RESET_PASSWORD)
                }
        }

        model.addAttribute("features", objectMapper.writeValueAsString(features))
        model.addAttribute("assetBundle", "login_bundle.js")
        return "template"
    }

    private fun defaultFeature() =
        mutableMapOf(
            ClientApplicationFeatures.SIGNUP.value to false,
            ClientApplicationFeatures.RESET_PASSWORD.value to false
        )

}