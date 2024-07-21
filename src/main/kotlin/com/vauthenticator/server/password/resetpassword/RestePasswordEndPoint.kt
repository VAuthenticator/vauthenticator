package com.vauthenticator.server.password.resetpassword

import com.fasterxml.jackson.databind.ObjectMapper
import com.vauthenticator.server.i18n.I18nMessageInjector
import com.vauthenticator.server.i18n.I18nScope
import com.vauthenticator.server.oauth2.clientapp.Scope
import com.vauthenticator.server.oauth2.clientapp.Scopes
import com.vauthenticator.server.role.PermissionValidator
import com.vauthenticator.server.ticket.TicketId
import jakarta.servlet.http.HttpSession
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.noContent
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@RestController
@SessionAttributes("clientId")
class ResetPasswordEndPoint(
    private val permissionValidator: PermissionValidator,
    private val sendResetPasswordMailChallenge: SendResetPasswordMailChallenge,
    private val resetAccountPassword: ResetAccountPassword
) {

    @PutMapping("/api/reset-password-challenge")
    fun sendVerifyMail(
        @RequestBody request: Map<String, String>,
        session: HttpSession,
        principal: JwtAuthenticationToken?
    ): ResponseEntity<Unit> {
        permissionValidator.validate(principal, session, Scopes.from(Scope.RESET_PASSWORD))
        sendResetPasswordMailChallenge.sendResetPasswordMailFor(request["email"]!!)
        return noContent().build()
    }

    @PutMapping("/api/reset-password/{ticket}")
    fun resetPassword(@PathVariable ticket: String, @RequestBody request: ResetPasswordRequest): ResponseEntity<Unit> {
        resetAccountPassword.resetPasswordFromMailChallenge(TicketId(ticket), request)
        return noContent().build()
    }

}

@Controller
class ResetPasswordController(
    private val i18nMessageInjector: I18nMessageInjector,
    private val objectMapper: ObjectMapper
) {

    @GetMapping("/reset-password/reset-password-challenge-sender")
    fun resetPasswordChallengeSenderPage(model: Model): String {
        i18nMessageInjector.setMessagedFor(I18nScope.RESET_PASSWORD_CHALLENGE_SENDER_PAGE, model)
        model.addAttribute("assetBundle", "resetPasswordChallengeSender_bundle.js")
        return "template"
    }

    @GetMapping("/reset-password/successful-reset-password-email-challenge")
    fun successfulResetPasswordMailChallengePage(model: Model): String {
        i18nMessageInjector.setMessagedFor(I18nScope.SUCCESSFUL_RESET_PASSWORD_CHALLENGE_SENDER_PAGE, model)
        model.addAttribute("assetBundle", "successfulResetPasswordMailChallenge_bundle.js")
        return "template"
    }

    @GetMapping("/reset-password/{ticket}")
    fun resetPasswordPage(@PathVariable ticket: String, model: Model): String {
        val metadata = mapOf("ticket" to ticket)

        i18nMessageInjector.setMessagedFor(I18nScope.RESET_PASSWORD_PAGE, model)
        model.addAttribute("metadata", objectMapper.writeValueAsString(metadata))
        model.addAttribute("assetBundle", "resetPassword_bundle.js")
        return "template"
    }

    @GetMapping("/reset-password/successful-password-reset")
    fun successfulResetPasswordPage(model: Model): String {
        i18nMessageInjector.setMessagedFor(I18nScope.SUCCESSFUL_RESET_PASSWORD_PAGE, model)
        model.addAttribute("assetBundle", "successfulPasswordReset_bundle.js")
        return "template"
    }
}

data class ResetPasswordRequest(val newPassword: String)