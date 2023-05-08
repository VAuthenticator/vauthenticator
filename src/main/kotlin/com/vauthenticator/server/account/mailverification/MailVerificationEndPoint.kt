package com.vauthenticator.server.account.mailverification

import com.vauthenticator.server.oauth2.clientapp.Scope
import com.vauthenticator.server.oauth2.clientapp.Scopes
import com.vauthenticator.server.role.PermissionValidator
import jakarta.servlet.http.HttpSession
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.*
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class MailVerificationEndPoint(
    private val permissionValidator: PermissionValidator,
    private val sendVerifyMailChallenge: SendVerifyMailChallenge
) {

    @PutMapping("/api/mail/{mail}/verify-challenge")
    fun sendVerifyMail(
        @PathVariable mail: String,
        httpSession: HttpSession,
        principal: JwtAuthenticationToken
    ): ResponseEntity<Unit> {
        permissionValidator.validate(principal, httpSession, Scopes.from(Scope.MAIL_VERIFY))
        sendVerifyMailChallenge.sendVerifyMail(mail)
        return noContent().build()
    }


}

@Controller
class MailVerificationController(private val verifyMailChallengeSent: VerifyMailChallengeSent) {

    @GetMapping("/mail-verify/{ticket}")
    fun verifyMail(@PathVariable ticket: String, model : Model): String {
        verifyMailChallengeSent.verifyMail(ticket)
        model.addAttribute("assetBundle", "successfulMailVerify_bundle.js")
        return "template"
    }

}