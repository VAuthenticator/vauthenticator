package it.valeriovaudi.vauthenticator.account.api

import it.valeriovaudi.vauthenticator.account.Account
import it.valeriovaudi.vauthenticator.account.repository.AccountRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity.status
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AccountEndPoint(private val accountRepository: AccountRepository) {

    @PostMapping("/api/accounts")
    fun signup(@RequestBody representation: FinalAccountRepresentation) =
            accountRepository.create(SignUpAccountConverter.fromRepresentationToSignedUpAccount(representation))
                    .let {
                        status(HttpStatus.CREATED).build<Unit>()
                    }

    @PutMapping("/api/accounts")
    fun save() = status(HttpStatus.INTERNAL_SERVER_ERROR).build<Unit>()

}


data class FinalAccountRepresentation(
        var email: String = "",
        var password: String = "",
        var firstName: String,
        var lastName: String,
        val authorities: List<String> = emptyList()
)

object SignUpAccountConverter {
    fun fromRepresentationToSignedUpAccount(representation: FinalAccountRepresentation): Account =
            Account(
                    accountNonExpired = true,
                    accountNonLocked = true,
                    credentialsNonExpired = true,
                    enabled = true,
                    username = representation.email,
                    password = representation.password,
                    firstName = representation.firstName,
                    lastName = representation.lastName,
                    email = representation.email,
                    emailVerified = true,
                    authorities = emptyList()
            )

}