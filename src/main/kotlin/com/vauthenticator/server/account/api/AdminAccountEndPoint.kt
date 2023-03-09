package com.vauthenticator.server.account.api

import com.vauthenticator.server.account.Account
import com.vauthenticator.server.account.repository.AccountRepository
import org.springframework.http.ResponseEntity.*
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
class AdminAccountEndPoint(private val accountRepository: AccountRepository) {

    @GetMapping("/api/admin/accounts/{email}/email")
    fun findAccountFor(@PathVariable email: String, authentication: Authentication) =
            ok(
                    accountRepository.accountFor(email)
                            .map { AccountConverter.fromDomainToAccountApiRepresentation(it) }
            )

    @PutMapping("/api/admin/accounts/{email}/email")
    fun saveAccount(@PathVariable email: String,
                    @RequestBody representation: AdminAccountApiRepresentation
    ) =
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

data class AdminAccountApiRepresentation(
        val accountLocked: Boolean = true,
        val enabled: Boolean = true,
        var email: String = "",
        val authorities: List<String> = emptyList()
)

object AccountConverter {
    fun fromDomainToAccountApiRepresentation(domain: Account): AdminAccountApiRepresentation =
            AdminAccountApiRepresentation(!domain.accountNonLocked, domain.enabled, domain.email, domain.authorities)

}