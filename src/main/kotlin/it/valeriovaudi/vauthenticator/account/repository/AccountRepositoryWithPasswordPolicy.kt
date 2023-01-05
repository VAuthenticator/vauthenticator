package it.valeriovaudi.vauthenticator.account.repository

import it.valeriovaudi.vauthenticator.account.Account
import it.valeriovaudi.vauthenticator.password.PasswordPolicy

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