package com.vauthenticator.server.account.mailverification

import com.vauthenticator.server.oauth2.clientapp.Scope
import com.vauthenticator.server.oauth2.clientapp.Scopes
import com.vauthenticator.server.role.PermissionValidator
import jakarta.servlet.http.HttpSession
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.noContent
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@RestController
class MailVerificationEndPoint(
    private val permissionValidator: PermissionValidator,
    private val sendVerifyMailChallenge: SendVerifyMailChallenge
) {

    @PutMapping("/api/verify-challenge")
    fun sendVerifyMail(
        @RequestBody request : Map<String,String>,
        httpSession: HttpSession,
        principal: JwtAuthenticationToken
    ): ResponseEntity<Unit> {
        permissionValidator.validate(principal, httpSession, Scopes.from(Scope.MAIL_VERIFY))
        sendVerifyMailChallenge.sendVerifyMail(request["mail"]!!)
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