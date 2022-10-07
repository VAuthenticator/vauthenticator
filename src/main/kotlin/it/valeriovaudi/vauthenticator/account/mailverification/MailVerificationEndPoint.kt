package it.valeriovaudi.vauthenticator.account.mailverification

import it.valeriovaudi.vauthenticator.extentions.clientAppId
import it.valeriovaudi.vauthenticator.mail.MailType
import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class MailVerificationEndPoint(private val sendVerifyMailChallenge: SendVerifyMailChallenge) {

    @PutMapping("/api/mail/{mail}/verify-challenge")
    fun sendVerifyMail(@PathVariable mail: String, principal: JwtAuthenticationToken) =
            sendVerifyMailChallenge.sendVerifyMail(mail, principal.clientAppId())
                    .let { ResponseEntity.noContent().build<Unit>() }


}

@Controller
class MailVerificationController(private val verifyMailChallengeSent: VerifyMailChallengeSent) {


    @GetMapping("/api/mail-verify/{ticket}")
    fun verifyMail(@PathVariable ticket: String) =
            verifyMailChallengeSent.verifyMail(ticket)
                    .let { MailType.SUCCESSFUL_EMAIL_VERIFICATION }

}