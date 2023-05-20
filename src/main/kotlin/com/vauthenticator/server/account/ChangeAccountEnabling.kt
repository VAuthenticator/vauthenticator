package com.vauthenticator.server.account

import com.vauthenticator.server.account.repository.AccountRepository

class ChangeAccountEnabling(private val accountRepository: AccountRepository) {
    fun execute(
        email: String,
        accountLocked: Boolean,
        enabled: Boolean,
        authorities: Set<String>
    ) = accountRepository.accountFor(email)
        .ifPresent { account ->
            accountRepository.save(
                account.copy(
                    accountNonLocked = !accountLocked,
                    enabled = enabled,
                    authorities = authorities
                )
            )
        }

}