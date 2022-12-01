package it.valeriovaudi.vauthenticator.mfa

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
class MfaController(
    private val successHandler: AuthenticationSuccessHandler,
    private val otpMfaSender: OtpMfaSender,
    private val otpMfaVerifier: OtpMfaVerifier
) {

    private val logger = LoggerFactory.getLogger(MfaController::class.java)

    @GetMapping("/mfa-challenge")
    fun view(authentication: Authentication): String {
        otpMfaSender.sendMfaChallenge(authentication.name)
        return "mfa/index"
    }


    @PostMapping("/mfa-challenge")
    fun processSecondFactor(
        @RequestParam("mfa-code") mfaCode: String,
        authentication: MfaAuthentication,
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {
        try {
            otpMfaVerifier.verifyMfaChallengeFor(authentication.name, MfaChallenge(mfaCode))

            SecurityContextHolder.getContext().authentication = authentication.delegate
            successHandler.onAuthenticationSuccess(request, response, authentication.delegate)
        } catch (e: Exception) {
            logger.error(e.message, e)
            response.sendRedirect("/mfa-challenge?error")
        }
    }
}

@RestController
class MfaApi(
    private val otpMfaSender: OtpMfaSender,
) {
    @PutMapping("/mfa-challenge/send")
    fun sendAgain(authentication: Authentication) {
        otpMfaSender.sendMfaChallenge(authentication.name)
    }
}