package it.valeriovaudi.vauthenticator.mfa

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping


@Controller
class MfaController(
    private val successHandler: AuthenticationSuccessHandler,
    private val failureHandler: AuthenticationFailureHandler
) {

    @GetMapping("/mfa-challenge")
    fun view() = "mfa/index"


    @PostMapping("/mfa-challenge")
    fun processSecondFactor(
        authentication: MfaAuthentication?,
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {
        if (true) {
            successHandler.onAuthenticationSuccess(request, response, authentication!!.delegate)
        } else {
            failureHandler.onAuthenticationFailure(request, response, BadCredentialsException("bad credentials"))
        }
    }
}
