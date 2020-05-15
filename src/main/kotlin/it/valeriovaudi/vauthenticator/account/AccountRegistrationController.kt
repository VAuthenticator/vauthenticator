package it.valeriovaudi.vauthenticator.account

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping


@Controller
class AccountRegistrationController(@Value("\${feature.signup.enabled:false}") private val signupEnabled: Boolean,
                                    private val accountRegistration: AccountRegistration) {

    @GetMapping("/signup")
    fun view() = if (signupEnabled) "signup" else "error/404"

    @GetMapping("/thank-you")
    fun thankYou() = if (signupEnabled) "thank-you" else "error/404"

    @PostMapping("/signup")
    fun signup(account: AccountRepresentation): String =
            if (signupEnabled) {
                accountRegistration.execute(AccountConverter.fromRepresentationToDomain(account))
                "redirect:thank-you"
            } else {
                "redirect:error/404"
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