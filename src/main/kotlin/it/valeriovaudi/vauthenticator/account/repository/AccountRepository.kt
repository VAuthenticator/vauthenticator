package it.valeriovaudi.vauthenticator.account.repository

import it.valeriovaudi.vauthenticator.account.Account
import java.util.*

interface AccountRepository {
    fun findAll(eagerRolesLoad: Boolean = false): List<Account>
    fun accountFor(username: String): Optional<Account>
    fun save(account: Account)
    fun create(account: Account)
}

class AccountRegistrationException(message: String, e: RuntimeException) : RuntimeException(message, e)