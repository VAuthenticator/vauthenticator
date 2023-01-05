package it.valeriovaudi.vauthenticator.account.repository

import it.valeriovaudi.vauthenticator.account.Account
import it.valeriovaudi.vauthenticator.cache.CacheContentConverter
import it.valeriovaudi.vauthenticator.cache.CacheOperation
import java.util.*

class CachedAccountRepository(
    private val cacheContentConverter: CacheContentConverter<Account>,
    private val cacheOperation: CacheOperation<String, String>,
    private val delegate: AccountRepository
) : AccountRepository by delegate {

    override fun accountFor(username: String): Optional<Account> {
        return cacheOperation.get(username)
            .flatMap { Optional.of(cacheContentConverter.getObjectFromCacheContentFor(it)) }
            .or {
                val loadedAccount = delegate.accountFor(username)
                loadedAccount.ifPresent {
                    cacheOperation.put(
                        username,
                        cacheContentConverter.loadableContentIntoCacheFor(it)
                    )
                }
                loadedAccount
            }
    }

    override fun save(account: Account) {
        cacheOperation.evict(account.email)
        delegate.save(account)
    }

}