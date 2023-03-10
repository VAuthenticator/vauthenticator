package com.vauthenticator.server.account

import com.vauthenticator.server.account.repository.AccountRepository
import java.security.Principal
import java.util.*

class ChangeAccountEnabling(private val accountRepository: AccountRepository) {
    fun execute(
        email: String,
        accountLocked: Boolean,
        enabled: Boolean,
        authorities: List<String>
    ): Optional<Unit> = accountRepository.accountFor(email)
        .map { account ->
            accountRepository.save(
                account.copy(
                    accountNonLocked = !accountLocked,
                    enabled = enabled,
                    authorities = authorities
                )
            )
        }

}