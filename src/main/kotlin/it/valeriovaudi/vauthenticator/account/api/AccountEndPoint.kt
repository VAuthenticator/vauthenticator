package it.valeriovaudi.vauthenticator.account.api

import it.valeriovaudi.vauthenticator.account.Account
import it.valeriovaudi.vauthenticator.account.usecase.SignUpUseCase
import it.valeriovaudi.vauthenticator.extentions.stripBearerPrefix
import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientApplication
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity.status
import org.springframework.web.bind.annotation.*

@RestController
class AccountEndPoint(private val signUpUseCase: SignUpUseCase) {

    @PostMapping("/api/accounts")
    fun signup(@RequestHeader("Authorization") authorization : String, @RequestBody representation: FinalAccountRepresentation) =
            SignUpAccountConverter.fromRepresentationToSignedUpAccount(representation)
                    .let {
                        signUpUseCase.execute(ClientApplication.clientAppIdFrom(authorization.stripBearerPrefix()), it)
                    }
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