package it.valeriovaudi.vauthenticator.account.repository

import it.valeriovaudi.vauthenticator.account.Account
import java.util.*

interface AccountRepository {
    fun findAll(eagerRolesLoad: Boolean = false): List<Account>
    fun accountFor(username: String): Optional<Account>
    fun save(account: Account)
    abstract fun create(account: Account)
}

class AccountRegistrationException(e: RuntimeException) : RuntimeException(e)