package it.valeriovaudi.vauthenticator.cache

import it.valeriovaudi.vauthenticator.account.Account
import java.time.Duration

interface CacheOperation {
    fun put(account: Account, ttlInSeconds: Duration)
}