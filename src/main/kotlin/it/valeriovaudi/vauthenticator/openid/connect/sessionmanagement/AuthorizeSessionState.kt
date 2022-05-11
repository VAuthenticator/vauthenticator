package it.valeriovaudi.vauthenticator.openid.connect.sessionmanagement

import it.valeriovaudi.vauthenticator.extentions.toSha256
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationToken
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings
import org.springframework.security.web.RedirectStrategy
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.util.UriComponentsBuilder
import java.util.*
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

fun sendAuthorizationResponse(
        factory: SessionManagementFactory,
        redirectStrategy: RedirectStrategy
) = { request: HttpServletRequest,
      response: HttpServletResponse,
      authentication: Authentication ->

    val authorizationCodeRequestAuthentication = authentication as OAuth2AuthorizationCodeRequestAuthenticationToken
    val uriBuilder = UriComponentsBuilder
            .fromUriString(authorizationCodeRequestAuthentication.redirectUri!!)
            .queryParam(OAuth2ParameterNames.CODE, authorizationCodeRequestAuthentication.authorizationCode!!.tokenValue)
    if (StringUtils.hasText(authorizationCodeRequestAuthentication.state)) {
        uriBuilder.queryParam(OAuth2ParameterNames.STATE, authorizationCodeRequestAuthentication.state)
    }

    val sessionState = factory.sessionStateFor(request, authentication)
    val cookie = factory.opbsCookieFor(request)
    response.addCookie(cookie)

    uriBuilder.queryParam("session_state", sessionState)

    redirectStrategy.sendRedirect(request, response, uriBuilder.toUriString())
}

class SessionManagementFactory(private val providerSettings: ProviderSettings) {

    fun opbsCookieFor(request: HttpServletRequest): Cookie {
        val cookie = Cookie("opbs", opbsCookieValue(request))
        cookie.path = request.contextPath
        return cookie
    }

    fun opbsCookieValue(request: HttpServletRequest): String {
        val opbs: String = (request.session.getAttribute("opbs_cookie_value") ?: "") as String
        if (opbs.isEmpty()) {
            val opbs = UUID.randomUUID().toString();
            request.session.setAttribute("opbs_cookie_value", opbs)
        }

        return opbs;
    }

    fun sessionStateFor(request: HttpServletRequest, authentication: OAuth2AuthorizationCodeRequestAuthenticationToken): String {
        val clientId = authentication.clientId
        val issuer = providerSettings.issuer
        val salt = saltFor(request)

        return "$clientId $issuer ${opbsCookieValue(request)} $salt".toSha256() + ".$salt"
    }

    fun saltFor(request: HttpServletRequest) =
            opbsCookieValue(request).toSha256()


}

@Controller
class SessionManagementIFrameController(private val providerSettings: ProviderSettings) {


    @CrossOrigin("*")
    @GetMapping("/session/management")
    fun sessionManagerIframe(model: Model): String {
        val issuer = providerSettings.issuer

        model.addAttribute("issuer", issuer)
        return "session/management"
    }
}