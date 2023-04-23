package com.vauthenticator.server.account.resetpassword

import com.fasterxml.jackson.databind.ObjectMapper
import com.vauthenticator.server.account.tiket.VerificationTicket
import com.vauthenticator.server.oauth2.clientapp.*
import com.vauthenticator.server.role.PermissionValidator
import jakarta.servlet.http.HttpSession
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.*
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@SessionAttributes("clientId")
class ResetPasswordEndPoint(
    private val permissionValidator: PermissionValidator,
    private val sendResetPasswordMailChallenge: SendResetPasswordMailChallenge,
    private val resetAccountPassword: ResetAccountPassword
) {

    @PutMapping("/api/mail/{mail}/reset-password-challenge")
    fun sendVerifyMail2(@PathVariable mail: String, session: HttpSession, principal: JwtAuthenticationToken?): ResponseEntity<Unit> {
        permissionValidator.validate(principal, session, Scopes.from(Scope.RESET_PASSWORD))
        sendResetPasswordMailChallenge.sendResetPasswordMailFor(mail)
        return noContent().build()
    }

    @PutMapping("/api/reset-password/{ticket}")
    fun resetPassword(@PathVariable ticket: String, @RequestBody request: ResetPasswordRequest): ResponseEntity<Unit> {
        resetAccountPassword.resetPasswordFromMailChallenge(VerificationTicket(ticket), request)
        return noContent().build()
    }

}

@Controller
class ResetPasswordController(private val objectMapper: ObjectMapper) {


    @GetMapping("/reset-password/reset-password-challenge-sender")
    fun resetPasswordChallengeSenderPage(model: Model): String {
        model.addAttribute("assetBundle", "resetPasswordChallengeSender_bundle.js")
        return "template"
    }

    @GetMapping("/reset-password/successful-reset-password-mail-challenge")
    fun successfulResetPasswordMailChallengePage(model: Model): String {
        model.addAttribute("assetBundle", "successfulResetPasswordMailChallenge_bundle.js")
        return "template"
    }

    @GetMapping("/reset-password/{ticket}")
    fun resetPasswordPage(@PathVariable ticket: String, model: Model): String {
        val metadata = mapOf("ticket" to ticket)
        model.addAttribute("metadata", objectMapper.writeValueAsString(metadata))
        model.addAttribute("assetBundle", "resetPassword_bundle.js")
        return "template"
    }

    @GetMapping("/reset-password/successful-password-reset")
    fun successfulResetPasswordPage(model: Model): String {
        model.addAttribute("assetBundle", "successfulPasswordReset_bundle.js")
        return "template"
    }
}

data class ResetPasswordRequest(val newPassword: String)