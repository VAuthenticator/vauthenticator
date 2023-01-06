package com.vauthenticator.server.account.repository

import com.vauthenticator.server.account.Account
import com.vauthenticator.server.password.PasswordPolicy

class AccountRepositoryWithPasswordPolicy(
    private val accountRepository: AccountRepository,
    private val passwordPolicy: PasswordPolicy
) : AccountRepository by accountRepository {

    override fun create(account: Account) {
        passwordPolicy.accept(account.password)
        accountRepository.create(account)
    }

    override fun save(account: Account) {
        passwordPolicy.accept(account.password)
        accountRepository.save(account)
    }
}