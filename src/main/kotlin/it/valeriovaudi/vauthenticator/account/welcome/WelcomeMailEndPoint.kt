package it.valeriovaudi.vauthenticator.account.welcome

import it.valeriovaudi.vauthenticator.account.AccountNotFoundException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class WelcomeMailEndPoint(
    private val sayWelcome: SayWelcome
) {

    @PutMapping("/api/sign-up/mail/{mail}/welcome")
    fun welcome(@PathVariable mail: String) =
        sayWelcome.welcome(mail)
            .let {
                ResponseEntity.noContent().build<Unit>()
            }

    @ExceptionHandler(AccountNotFoundException::class)
    fun noAccountExceptionHAndler() =
        ResponseEntity.notFound().build<Unit>()
}