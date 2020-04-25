package it.valeriovaudi.vauthenticator.account

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping


@Controller
class AccountRegistrationController(private val accountRegistration: AccountRegistration) {

    @GetMapping("/signup")
    fun view() = "signup"

    @GetMapping("/thank-you")
    fun thankYou() = "thank-you"

    @PostMapping("/signup")
    fun signup(account: AccountRepresentation): String {
        accountRegistration.execute(AccountConverter.fromRepresentationToDomain(account))
        return "redirect:thank-you"
    }
}

object AccountConverter {
    fun fromRepresentationToDomain(representation: AccountRepresentation): Account = Account(
            email = representation.email,
            password = representation.password,
            username = representation.email,
            firstName = representation.firstName,
            lastName = representation.lastName,
            sub = representation.email,
            authorities = listOf("ROLE_USER"),
            accountNonExpired = false,
            accountNonLocked = false,
            credentialsNonExpired = false
    )

    fun fromDomainToRepresentation(account: Account): AccountRepresentation =
            AccountRepresentation(email = account.username,
                    password = "",
                    firstName = account.firstName,
                    lastName = account.lastName
            )
}

class AccountRepresentation(var email: String, var password: String, var firstName: String, var lastName: String)