package com.vauthenticator.server.mfa

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.util.*

@Controller
class MfaController(
    private val objectMapper: ObjectMapper,
    private val successHandler: AuthenticationSuccessHandler,
    private val otpMfaSender: OtpMfaSender,
    private val otpMfaVerifier: OtpMfaVerifier
) {

    private val logger = LoggerFactory.getLogger(MfaController::class.java)

    @GetMapping("/mfa-challenge/send")
    fun view(authentication: Authentication): String {
        otpMfaSender.sendMfaChallenge(authentication.name)
        return "redirect:/mfa-challenge"
    }

    @GetMapping("/mfa-challenge")
    fun view(
        model: Model,
        authentication: Authentication,
        httpServletRequest: HttpServletRequest
    ): String {
        val errors = errorMessageFor(httpServletRequest)
        model.addAttribute("errors", objectMapper.writeValueAsString(errors))

        model.addAttribute("assetBundle", "mfa_bundle.js")
        return "template"
    }

    private fun errorMessageFor(httpServletRequest: HttpServletRequest) =
        if (hasBadLoginFrom(httpServletRequest)) {
            mapOf("mfa-challenge" to "Ops! the MFA code provided is wrong or expired")
        } else {
            emptyMap()
        }

    private fun hasBadLoginFrom(httpServletRequest: HttpServletRequest) =
        !Optional.ofNullable(httpServletRequest.session.getAttribute("MFA_SPRING_SECURITY_LAST_EXCEPTION")).isEmpty

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
            request.session.setAttribute("MFA_SPRING_SECURITY_LAST_EXCEPTION", MfaException("Invalid mfa code"))

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