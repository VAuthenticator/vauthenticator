package com.vauthenticator.server.account.api

import com.vauthenticator.server.account.Account
import com.vauthenticator.server.account.Date
import com.vauthenticator.server.account.Phone
import com.vauthenticator.server.account.UserLocale
import com.vauthenticator.server.account.api.SignUpAccountConverter.fromRepresentationToSignedUpAccount
import com.vauthenticator.server.account.repository.AccountRepository
import com.vauthenticator.server.account.signup.SignUpUseCase
import com.vauthenticator.server.extentions.clientAppId
import com.vauthenticator.server.extentions.oauth2ClientId
import com.vauthenticator.server.oauth2.clientapp.ClientAppId
import jakarta.servlet.http.HttpSession
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.status
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.web.bind.annotation.*
import java.util.*
import java.util.Optional.ofNullable

@RestController
@SessionAttributes("clientId")
class AccountEndPoint(
    private val signUpUseCase: SignUpUseCase,
    private val accountRepository: AccountRepository
) {
    val logger: Logger = LoggerFactory.getLogger(AccountEndPoint::class.java)

    @PostMapping("/api/accounts")
    fun signup(
        principal: JwtAuthenticationToken?,
        session: HttpSession,
        @RequestBody representation: FinalAccountRepresentation
    ): ResponseEntity<Unit> {
        val account = fromRepresentationToSignedUpAccount(representation)
        clientAppIdFrom(principal, session)
            .map { executeSignUp(it, account) }
            .orElseThrow()
        return status(HttpStatus.CREATED).build()
    }

    private fun clientAppIdFrom(
        principal: JwtAuthenticationToken?,
        session: HttpSession
    ): Optional<ClientAppId> =
        ofNullable(principal).map { it.clientAppId() }.or { session.oauth2ClientId() }

    private fun executeSignUp(clientAppId: ClientAppId, account: Account) {
        signUpUseCase.execute(clientAppId, account)
    }

    @PutMapping("/api/accounts")
    fun save(
        principal: JwtAuthenticationToken,
        @RequestBody representation: FinalAccountRepresentation
    ): ResponseEntity<Unit> {
        //todo should be an use case start
        val userName = principal.name

        logWarningForNotEmptyUserNameInRequestBodyFor(representation)
        return accountRepository.accountFor(userName)
            .map { account ->
                val filledRepresentation = representation.copy(
                    email = userName,
                    password = account.password,
                    authorities = account.authorities
                )
                val accountToBeSaved = fromRepresentationToSignedUpAccount(
                    filledRepresentation
                ).copy(
                    accountNonExpired = account.accountNonExpired,
                    accountNonLocked = account.accountNonLocked,
                    credentialsNonExpired = account.credentialsNonExpired,
                    enabled = account.enabled,
                    emailVerified = account.emailVerified,
                )
                accountRepository.save(accountToBeSaved)
                //todo should be an use case end
                ResponseEntity.noContent().build<Unit>()
            }
            .orElseGet {
                ResponseEntity.noContent().build()
            }

    }

    private fun logWarningForNotEmptyUserNameInRequestBodyFor(representation: FinalAccountRepresentation) {
        if (representation.email.isNotEmpty()) {
            logger.warn("there is an email in the body.............. it will be ignored in favour of the access token identity")
        }
    }

}


data class FinalAccountRepresentation(
    var email: String = "",
    var password: String = "",
    var firstName: String = "",
    var lastName: String = "",
    var birthDate: String = "",
    var phone: String = "",
    var locale: String = "",
    val authorities: List<String> = emptyList()
)

object SignUpAccountConverter {
    fun fromRepresentationToSignedUpAccount(representation: FinalAccountRepresentation): Account =
        Account(
            accountNonExpired = true,
            accountNonLocked = false,
            credentialsNonExpired = true,
            enabled = false,
            emailVerified = false,
            username = representation.email,
            password = representation.password,
            firstName = representation.firstName,
            lastName = representation.lastName,
            email = representation.email,
            authorities = representation.authorities,
            birthDate = Date.isoDateFor(representation.birthDate),
            phone = Phone.phoneFor(representation.phone),
            locale = UserLocale.localeFrom(representation.locale),
        )
}