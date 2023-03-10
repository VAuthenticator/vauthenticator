package com.vauthenticator.server.account

import com.vauthenticator.server.account.repository.AccountRepository
import java.security.Principal
import java.util.*

class SaveAccount(private val accountRepository: AccountRepository) {
    fun execute(principal: Principal, account: Account): Optional<Unit> {
        return accountRepository.accountFor(principal.name)
            .map {
                val accountToBeSaved = account.copy(
                    email = principal.name,
                    password = it.password,
                    authorities = it.authorities,
                    accountNonExpired = it.accountNonExpired,
                    accountNonLocked = it.accountNonLocked,
                    credentialsNonExpired = it.credentialsNonExpired,
                    enabled = it.enabled,
                    emailVerified = it.emailVerified,
                )
                accountRepository.save(accountToBeSaved)
            }
    }
}