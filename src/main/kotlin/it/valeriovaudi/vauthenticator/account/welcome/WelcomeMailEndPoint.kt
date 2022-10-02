package it.valeriovaudi.vauthenticator.account.welcome

import it.valeriovaudi.vauthenticator.account.repository.AccountRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class WelcomeMailEndPoint(
        private val accountRepository: AccountRepository,
        private val welcomeMailSender: WelcomeMailSender
) {

    @GetMapping("/sign-up/mail/{mail}/welcome")
    fun welcome(@PathVariable mail: String) =
            accountRepository.accountFor(mail)
                    .map {
                        welcomeMailSender.sendFor(it)
                        ResponseEntity.noContent().build<Unit>()
                    }.orElse(ResponseEntity.notFound().build())


}