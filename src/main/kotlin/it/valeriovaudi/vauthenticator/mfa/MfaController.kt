package it.valeriovaudi.vauthenticator.mfa

import it.valeriovaudi.vauthenticator.account.repository.AccountRepository
import it.valeriovaudi.vauthenticator.mail.MailSenderService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class MfaController(
    private val otp: OtpMfa,
    private val accountRepository: AccountRepository,
    private val mfaMailSender: MailSenderService,

    private val successHandler: AuthenticationSuccessHandler,
    private val failureHandler: AuthenticationFailureHandler
) {

    @GetMapping("/mfa-challenge")
    fun view(authentication: Authentication): String {
        val account = accountRepository.accountFor(authentication.name).get()
        val mfaCode = otp.generateSecretKeyFor(account)
        mfaMailSender.sendFor(account, mapOf("mfaCode" to mfaCode))
        return "mfa/index"
    }


    @PostMapping("/mfa-challenge")
    fun processSecondFactor(
        @RequestParam("mfa-code") mfaCode: String,
        authentication: MfaAuthentication,
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {
        val account = accountRepository.accountFor(authentication.name).get()

        try {
            otp.verify(account, MfaChallenge(mfaCode)))
            SecurityContextHolder.getContext().authentication = authentication.delegate
            successHandler.onAuthenticationSuccess(request, response, authentication.delegate)
        } catch (e: Exception) {
            failureHandler.onAuthenticationFailure(request, response, BadCredentialsException("bad credentials"))
        }
    }
}
