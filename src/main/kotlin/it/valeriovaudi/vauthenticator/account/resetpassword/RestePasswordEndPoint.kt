package it.valeriovaudi.vauthenticator.account.resetpassword

import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.*
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@RestController
class ResetPasswordEndPoint(private val sendResetPasswordMailChallenge: SendResetPasswordMailChallenge,
                            private val sendResetPasswordChallengeSent: SendResetPasswordChallengeSent) {

    @PutMapping("/api/mail/{mail}/rest-password-challenge")
    fun sendVerifyMail(@PathVariable mail: String) =
            sendResetPasswordMailChallenge.sendResetPasswordMail(mail)
                    .let { noContent().build<Unit>() }

    @PutMapping("/api/reset-password/{ticket}")
    fun resetPassword(@PathVariable ticket: String, @RequestBody request: ResetPasswordRequest): ResponseEntity<Unit> {
        sendResetPasswordChallengeSent.resetPassword(ticket, request)
        return noContent().build()
    }
}

@Controller
class ResetPasswordController {

    @GetMapping("/reset-password/{ticket}")
    fun resetPasswordPage(@PathVariable ticket: String, model: Model): String {
        model.addAttribute("ticket", ticket)
        return "account/reset-password/reset-password"
    }

    @GetMapping("/reset-password/successful-password-reset")
    fun successfulResetPasswordPage(): String {
        return "account/reset-password/successful-password-reset"
    }
}

data class ResetPasswordRequest(val newPassword: String)