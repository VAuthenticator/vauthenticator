package it.valeriovaudi.vauthenticator.mfa

import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import java.io.IOException

class MfaAuthenticationHandler(url: String?) : AuthenticationSuccessHandler,
    AuthenticationFailureHandler {
    private val successHandler: AuthenticationSuccessHandler

    init {
        val successHandler = SimpleUrlAuthenticationSuccessHandler(url)
        successHandler.setAlwaysUseDefaultTargetUrl(true)
        this.successHandler = successHandler
    }

    @Throws(IOException::class, ServletException::class)
    override fun onAuthenticationFailure(
        request: HttpServletRequest, response: HttpServletResponse,
        exception: AuthenticationException
    ) {
        val anonymous: Authentication = AnonymousAuthenticationToken(
            "key", "anonymousUser",
            AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS")
        )
        saveMfaAuthentication(request, response, MfaAuthentication(anonymous))
    }

    @Throws(IOException::class, ServletException::class)
    override fun onAuthenticationSuccess(
        request: HttpServletRequest, response: HttpServletResponse,
        authentication: Authentication
    ) {
        saveMfaAuthentication(request, response, authentication)
    }

    @Throws(IOException::class, ServletException::class)
    private fun saveMfaAuthentication(
        request: HttpServletRequest, response: HttpServletResponse,
        authentication: Authentication
    ) {
        SecurityContextHolder.getContext().authentication = MfaAuthentication(authentication)
        successHandler.onAuthenticationSuccess(request, response, authentication)
    }
}
