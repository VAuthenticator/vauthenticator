package com.vauthenticator.server.mfa

import com.vauthenticator.server.extentions.hasEnoughScopes
import com.vauthenticator.server.oauth2.clientapp.ClientAppId
import com.vauthenticator.server.oauth2.clientapp.ClientApplicationRepository
import com.vauthenticator.server.oauth2.clientapp.Scope
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler

class MfaAuthenticationHandler(private val clientApplicationRepository: ClientApplicationRepository, url: String) :
    AuthenticationSuccessHandler {
    private val withMfaSuccessHandler: AuthenticationSuccessHandler
    private val withoutMfaSuccessHandler: AuthenticationSuccessHandler

    init {
        val withMfaSuccessHandler = SimpleUrlAuthenticationSuccessHandler(url)
        withMfaSuccessHandler.setAlwaysUseDefaultTargetUrl(true)
        this.withMfaSuccessHandler = withMfaSuccessHandler
        this.withoutMfaSuccessHandler = SavedRequestAwareAuthenticationSuccessHandler()
    }

    override fun onAuthenticationSuccess(
        request: HttpServletRequest, response: HttpServletResponse,
        authentication: Authentication
    ) {

        val clientId = request.session.getAttribute("clientId") as String
        val isMfaRequired = clientApplicationRepository.findOne(ClientAppId(clientId))
            .filter { it.hasEnoughScopes(Scope.MFA_ALWAYS) }
            .isPresent

        if (isMfaRequired) {
            SecurityContextHolder.getContext().authentication = MfaAuthentication(authentication)
            withMfaSuccessHandler.onAuthenticationSuccess(request, response, authentication)
        } else {
            withoutMfaSuccessHandler.onAuthenticationSuccess(request, response, authentication)
        }
    }

}
