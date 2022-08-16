package it.valeriovaudi.vauthenticator.account.signup

import it.valeriovaudi.vauthenticator.account.repository.AccountRepository
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
@SessionAttributes("features")
class SignUpController {

    @GetMapping("/sign-up")
    fun view(@ModelAttribute("features") features: List<String>, model: Model): String {
        return "signup"
    }
}


@RestController
class SignUpMailsEndPoint(
        private val accountRepository: AccountRepository,
        private val signUpConfirmationMailSender: SignUpConfirmationMailSender
) {

    @GetMapping("/sign-up/mail/{mail}/welcome")
    fun welcome(@PathVariable mail: String) =
            accountRepository.accountFor(mail)
                    .map {
                        signUpConfirmationMailSender.sendConfirmation(it)
                        ResponseEntity.noContent().build<Unit>()
                    }.orElse(ResponseEntity.notFound().build())


}