package it.valeriovaudi.vauthenticator.account.repository

import it.valeriovaudi.vauthenticator.account.Account
import it.valeriovaudi.vauthenticator.cache.CacheOperation
import java.time.Duration
import java.util.*

class CachedAccountRepository(
    private val cacheOperation: CacheOperation<String, Account>,
    private val ttlInSeconds: Duration,
    private val delegate: AccountRepository
) : AccountRepository by delegate {

    override fun accountFor(username: String): Optional<Account> {
        return cacheOperation.get(username)
            .or {
                val loadedAccount = delegate.accountFor(username)
                loadedAccount.ifPresent { cacheOperation.put(username, it, ttlInSeconds) }
                loadedAccount
            }
    }

    override fun save(account: Account) {
        cacheOperation.evict(account.email)
        delegate.save(account)
    }

}