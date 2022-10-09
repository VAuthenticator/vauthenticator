package it.valeriovaudi.vauthenticator.account.resetpassword

import it.valeriovaudi.vauthenticator.extentions.clientAppId
import org.springframework.http.ResponseEntity.*
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@RestController
class ResetPasswordEndPoint(private val sendResetPasswordMailChallenge: SendResetPasswordMailChallenge) {

    @PutMapping("/api/mail/{mail}/rest-password-challenge")
    fun sendVerifyMail(@PathVariable mail: String, principal: JwtAuthenticationToken) =
            sendResetPasswordMailChallenge.sendResetPasswordMail(mail, principal.clientAppId())
                    .let { noContent().build<Unit>() }

}

@Controller
class ResetPasswordController(private val sendResetPasswordChallengeSent: SendResetPasswordChallengeSent) {

    @GetMapping("/reset-password/{ticket}")
    fun verifyMail(@PathVariable ticket: String, @RequestBody request : ResetPasswordRequest): String {
        sendResetPasswordChallengeSent.resetPassword(ticket, request)
        return "account/reset-password/successful-password-reset"
    }

}

data class ResetPasswordRequest(val newPassword : String)