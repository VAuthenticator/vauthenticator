package com.vauthenticator.server.account.api

import com.vauthenticator.server.account.Account
import com.vauthenticator.server.account.AccountMandatoryAction
import com.vauthenticator.server.account.AccountUpdateAdminAction
import com.vauthenticator.server.account.AdminAccountApiRequest
import com.vauthenticator.server.account.repository.AccountRepository
import org.springframework.http.ResponseEntity.noContent
import org.springframework.http.ResponseEntity.ok
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
class AdminApiAccountEndPoint(
    private val accountRepository: AccountRepository,
    private val accountUpdateAdminAction: AccountUpdateAdminAction
) {

    @GetMapping("/api/admin/accounts/{email}/email")
    fun findAccountFor(@PathVariable email: String, authentication: Authentication) =
        ok(
            accountRepository.accountFor(email)
                .map { AdminApiAccountApiConverter.fromDomainToAccountAdminApiRepresentation(it) }
        )

    @PutMapping("/api/admin/accounts")
    fun saveAccount(@RequestBody representation: AdminAccountApiRequest) =
        accountUpdateAdminAction.execute(representation)
            .let { noContent().build<Unit>() }

}

data class AdminApiAccountApiRepresentation(
    val accountLocked: Boolean = true,
    val enabled: Boolean = true,
    var email: String = "",
    val authorities: Set<String> = emptySet(),
    val mandatoryAction: String = AccountMandatoryAction.NO_ACTION.name
)

object AdminApiAccountApiConverter {
    fun fromDomainToAccountAdminApiRepresentation(domain: Account): AdminApiAccountApiRepresentation =
        AdminApiAccountApiRepresentation(
            !domain.accountNonLocked,
            domain.enabled,
            domain.email,
            domain.authorities,
            domain.mandatoryAction.name
        )

}