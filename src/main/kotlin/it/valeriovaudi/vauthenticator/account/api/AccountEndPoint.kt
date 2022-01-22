package it.valeriovaudi.vauthenticator.account.api

import it.valeriovaudi.vauthenticator.account.Account
import it.valeriovaudi.vauthenticator.account.repository.AccountRepository
import it.valeriovaudi.vauthenticator.config.adminRole
import org.springframework.http.ResponseEntity.*
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.web.bind.annotation.*

@RestController
class AccountEndPoint(private val accountRepository: AccountRepository) {

    @GetMapping("/api/accounts")
    fun findAll() =
            ok(
                    accountRepository.findAll()
                            .map { AccountConverter.fromDomainToAccountApiRepresentation(it, UsernamePasswordAuthenticationToken(null, null, listOf(SimpleGrantedAuthority("")))) }
            )

    @GetMapping("/api/accounts/{email}/email")
    fun findAccountFor(@PathVariable email: String, authentication: Authentication) =
            ok(
                    accountRepository.accountFor(email)
                            .map { AccountConverter.fromDomainToAccountApiRepresentation(it, UsernamePasswordAuthenticationToken(null, null, authentication.authorities)) }
            )

    @PutMapping("/api/accounts/{email}/email")
    fun saveAccount(@PathVariable email: String,
                    @RequestBody representation: AccountApiAdminRepresentation) =
            accountRepository.accountFor(email)
                    .map { account ->
                        accountRepository.save(
                                account.copy(
                                        accountNonLocked = !representation.accountLocked,
                                        enabled = representation.enabled,
                                        authorities = representation.authorities
                                )
                        )
                        noContent().build<Unit>()
                    }
                    .orElse(notFound().build())

    @PostMapping("/api/accounts/{email}/email")
    fun signUpAccount(@PathVariable email: String,
                      @RequestBody representation: AccountApiAdminRepresentation) =
            accountRepository.accountFor(email)
                    .map { account ->
                        accountRepository.save(
                                account.copy(
                                        accountNonLocked = !representation.accountLocked,
                                        enabled = representation.enabled,
                                        authorities = representation.authorities
                                )
                        )
                        noContent().build<Unit>()
                    }
                    .orElse(notFound().build())
}

sealed class AccountApiRepresentation

data class AccountApiAdminRepresentation(
        val accountLocked: Boolean = true,
        val enabled: Boolean = true,
        var email: String = "",
        val authorities: List<String> = emptyList()
) : AccountApiRepresentation()

data class AccountApiUserRepresentation(
        var email: String = "",
        var firstName: String,
        var lastName: String,
        val authorities: List<String> = emptyList()
) : AccountApiRepresentation()

object AccountConverter {
    fun fromDomainToAccountApiRepresentation(domain: Account, authentication: Authentication): AccountApiRepresentation =
            if (authentication.authorities.contains(SimpleGrantedAuthority(adminRole))) {
                AccountApiAdminRepresentation(!domain.accountNonLocked, domain.enabled, domain.email, domain.authorities)
            } else {
                AccountApiUserRepresentation(domain.email, domain.firstName, domain.lastName, domain.authorities)
            }

}