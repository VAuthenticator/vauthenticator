package com.vauthenticator.server.account.repository.jdbc

import com.vauthenticator.server.account.Account
import com.vauthenticator.server.account.repository.AccountRepository
import java.util.*


class JdbcAccountRepository : AccountRepository{
    override fun accountFor(username: String): Optional<Account> {
        TODO("Not yet implemented")
    }

    override fun save(account: Account) {
        TODO("Not yet implemented")
    }

    override fun create(account: Account) {
        TODO("Not yet implemented")
    }
}