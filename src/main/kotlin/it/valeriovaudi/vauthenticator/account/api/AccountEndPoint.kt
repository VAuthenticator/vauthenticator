package it.valeriovaudi.vauthenticator.account.api

import it.valeriovaudi.vauthenticator.account.Account
import it.valeriovaudi.vauthenticator.account.Date
import it.valeriovaudi.vauthenticator.account.Phone
import it.valeriovaudi.vauthenticator.account.repository.AccountRepository
import it.valeriovaudi.vauthenticator.account.signup.SignUpUseCase
import it.valeriovaudi.vauthenticator.extentions.stripBearerPrefix
import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientAppId
import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientApplication
import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientApplication.Companion.userNameFrom
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.status
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@SessionAttributes("clientId")
class AccountEndPoint(
        private val signUpUseCase: SignUpUseCase,
        private val accountRepository: AccountRepository
) {
    val logger: Logger = LoggerFactory.getLogger(AccountEndPoint::class.java)

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
        signUpUseCase.execute(clientAppId, account)
    }

    @PutMapping("/api/accounts")
    fun save(@RequestHeader("Authorization", required = false) authorization: String?,
             @RequestBody representation: FinalAccountRepresentation): ResponseEntity<Unit> {
        val accessToken = accessTokenFrom(authorization)

        if (accessToken.trim() == "") {
            return status(HttpStatus.UNAUTHORIZED).build()
        }

        val userName = userNameFrom(accessToken)

        return if (userName.isNotEmpty()) {
            if (representation.email.isNotEmpty()) {
                logger.warn("there is an email in the body.............. it will be ignored in favour of the access token identity")
            }

            accountRepository.accountFor(userName)
                    .map { account ->
                        val filledRepresentation = representation.copy(email = userName, password = account.password, authorities = account.authorities)
                        accountRepository.save(SignUpAccountConverter.fromRepresentationToSignedUpAccount(filledRepresentation))
                        ResponseEntity.noContent().build<Unit>()
                    }
                    .orElseGet {
                        ResponseEntity.noContent().build<Unit>()
                    }

        } else {
            status(HttpStatus.FORBIDDEN).build()
        }

    }

    private fun accessTokenFrom(authorization: String?) =
            Optional.ofNullable(authorization).map {
                try {
                    it.stripBearerPrefix()
                } catch (e: Exception) {
                    ""
                }

            }.orElse("")

}


data class FinalAccountRepresentation(
        var email: String = "",
        var password: String = "",
        var firstName: String,
        var lastName: String,
        var birthDate: String,
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
                    authorities = representation.authorities,
                    birthDate = Date.isoDateFor(representation.birthDate),
                    phone = Phone.phoneFor(representation.phone)
            )

}