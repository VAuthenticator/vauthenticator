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
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.util.UriComponentsBuilder
import java.util.*
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

fun sendAuthorizationResponse(
        providerSettings: ProviderSettings,
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

    val opbsCookie = opbsCookieValue()
    val sessionState = sessionStateFor(authentication, providerSettings, opbsCookie)
    val cookie = cookieFor(opbsCookie, request)
    response.addCookie(cookie)

    uriBuilder.queryParam("session_state", sessionState)

    redirectStrategy.sendRedirect(request, response, uriBuilder.toUriString())
}

private fun cookieFor(opbsCookie: String, request: HttpServletRequest): Cookie {
    val cookie = Cookie("opbs", opbsCookie)
    cookie.maxAge = 2592000
    cookie.path = request.contextPath
//    cookie.isHttpOnly = true
    return cookie
}

private fun opbsCookieValue() = UUID.randomUUID().toString()

fun sessionStateFor(authentication: OAuth2AuthorizationCodeRequestAuthenticationToken,
                    providerSettings: ProviderSettings,
                    opbsCookie: String): String {
    val clientId = authentication.clientId
    val issuer = providerSettings.issuer
    val salt = saltFor(opbsCookie)

    val ss = "$clientId $issuer $opbsCookie $salt".toSha256() + ".$salt"
    println(ss)
    return ss
}

fun saltFor(opbsCookie: String): String {
    return opbsCookie.toSha256()
}


@Controller
class SessionManagementIFrameController(private val providerSettings: ProviderSettings) {


    @GetMapping("/session/management")
    fun sessionManagerIframe(model: Model): String {
        val issuer = providerSettings.issuer

        model.addAttribute("issuer", issuer)
        return "session/management"
    }
}