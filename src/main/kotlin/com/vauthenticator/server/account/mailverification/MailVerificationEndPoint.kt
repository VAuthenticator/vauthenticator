package com.vauthenticator.server.account.mailverification

import com.vauthenticator.server.i18n.I18nMessageInjector
import com.vauthenticator.server.i18n.I18nScope
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
        @RequestBody request: Map<String, String>,
        httpSession: HttpSession,
        principal: JwtAuthenticationToken
    ): ResponseEntity<Unit> {
        permissionValidator.validate(principal, httpSession, Scopes.from(Scope.MAIL_VERIFY))
        //todo validate email field in body
        sendVerifyMailChallenge.sendVerifyMail(request["email"]!!)
        return noContent().build()
    }


}

@Controller
class MailVerificationController(
    private val i18nMessageInjector: I18nMessageInjector,
    private val verifyMailChallenge: VerifyMailChallenge
) {

    @GetMapping("/email-verify/{ticket}")
    fun verifyMail(@PathVariable ticket: String, model: Model): String {
        verifyMailChallenge.verifyMail(ticket)

        i18nMessageInjector.setMessagedFor(I18nScope.SUCCESSFUL_MAIL_VERIFY_PAGE, model)
        model.addAttribute("assetBundle", "successfulMailVerify_bundle.js")
        return "template"
    }

}