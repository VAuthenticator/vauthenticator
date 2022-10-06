package it.valeriovaudi.vauthenticator.account.mailverification

import it.valeriovaudi.vauthenticator.extentions.clientAppId
import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class MailVerificationEndPoint(private val mailVerificationUseCase: MailVerificationUseCase) {

    @PutMapping("/api/mail/{mail}/verify-challenge")
    fun sendVerifyMail(@PathVariable mail: String, principal: JwtAuthenticationToken) =
            mailVerificationUseCase.sendVerifyMail(mail, principal.clientAppId())
                    .let { ResponseEntity.noContent().build<Unit>() }

    fun verifyMail(@PathVariable mail: String): Unit = TODO()

}