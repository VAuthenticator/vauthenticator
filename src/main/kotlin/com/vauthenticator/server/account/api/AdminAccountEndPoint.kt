package com.vauthenticator.server.account.api

import com.vauthenticator.server.account.Account
import com.vauthenticator.server.account.ChangeAccountEnabling
import com.vauthenticator.server.account.repository.AccountRepository
import org.springframework.http.ResponseEntity.*
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
class AdminAccountEndPoint(
    private val accountRepository: AccountRepository,
    private val changeAccountEnabling: ChangeAccountEnabling
) {

    @GetMapping("/api/admin/accounts/{email}/email")
    fun findAccountFor(@PathVariable email: String, authentication: Authentication) =
        ok(
            accountRepository.accountFor(email)
                .map { AccountConverter.fromDomainToAccountApiRepresentation(it) }
        )

    @PutMapping("/api/admin/accounts/{email}/email")
    fun saveAccount(
        @PathVariable email: String,
        @RequestBody representation: AdminAccountApiRepresentation
    ) =
        changeAccountEnabling.execute(
            email,
            representation.accountLocked,
            representation.enabled,
            representation.authorities
        )
            .map { noContent().build<Unit>() }
            .orElse(noContent().build())

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