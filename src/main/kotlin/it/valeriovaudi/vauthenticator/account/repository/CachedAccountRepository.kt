package it.valeriovaudi.vauthenticator.account.repository

import it.valeriovaudi.vauthenticator.account.Account
import it.valeriovaudi.vauthenticator.cache.CacheOperation
import java.time.Duration
import java.util.*

class CachedAccountRepository(
    private val cacheOperation: CacheOperation,
    private val ttlInSeconds: Duration,
    private val delegate: AccountRepository
) : AccountRepository by delegate {

    override fun accountFor(username: String): Optional<Account> {
        val loadedAccount = delegate.accountFor(username)
        loadedAccount.ifPresent { cacheOperation.put(it, ttlInSeconds) }
        return loadedAccount
    }

    override fun save(account: Account) {
        TODO("Not yet implemented")
    }


}