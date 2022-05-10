package it.valeriovaudi.vauthenticator.account.api

import it.valeriovaudi.vauthenticator.account.Account
import it.valeriovaudi.vauthenticator.account.Date
import it.valeriovaudi.vauthenticator.account.Phone
import it.valeriovaudi.vauthenticator.account.signup.SignUpUseCase
import it.valeriovaudi.vauthenticator.extentions.stripBearerPrefix
import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientAppId
import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientApplication
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.status
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@SessionAttributes("clientId")
class AccountEndPoint(private val signUpUseCase: SignUpUseCase) {

    @PostMapping("/api/accounts")
    fun signup(@RequestHeader("Authorization", required = false) authorization: String?,
               @ModelAttribute("clientId") clientId: String?,
               @RequestBody representation: FinalAccountRepresentation): ResponseEntity<Unit> {
        SignUpAccountConverter.fromRepresentationToSignedUpAccount(representation)
                .let { account ->
                    Optional.ofNullable(authorization).map { executeSignUp(ClientApplication.clientAppIdFrom(it.stripBearerPrefix()), account) }
                            .orElseGet {
                                Optional.ofNullable(clientId).map { executeSignUp(ClientAppId(it), account) }
                                        .orElseThrow()
                            }
                }
        return status(HttpStatus.CREATED).build()
    }

    private fun executeSignUp(clientAppId: ClientAppId, account: Account) {
        signUpUseCase.execute(clientAppId , account)
    }

    @PutMapping("/api/accounts")
    fun save() = status(HttpStatus.INTERNAL_SERVER_ERROR).build<Unit>()

}


data class FinalAccountRepresentation(
        var email: String = "",
        var password: String = "",
        var firstName: String,
        var lastName: String,
        var birthDate : String,
        var phone: String,
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
                    authorities = emptyList(),
                    birthDate = Date.nullValue(),
                    phone = Phone.nullValue()
            )

}