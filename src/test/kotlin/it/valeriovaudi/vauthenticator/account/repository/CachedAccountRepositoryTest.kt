package it.valeriovaudi.vauthenticator.account.repository

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import it.valeriovaudi.vauthenticator.account.AccountTestFixture.anAccount
import it.valeriovaudi.vauthenticator.cache.CacheOperation
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.Duration
import java.util.*

@ExtendWith(MockKExtension::class)
internal class CachedAccountRepositoryTest {
    private val ttlInSeconds = Duration.ofSeconds(200)

    @MockK
    lateinit var delegate: AccountRepository

    @MockK
    lateinit var cacheOperation: CacheOperation

    lateinit var underTest: AccountRepository

    @BeforeEach
    fun setUp() {
        underTest = CachedAccountRepository(cacheOperation, ttlInSeconds, delegate)
    }

    @Test
    internal fun `when an account is found and loaded into the cache`() {
        val account = anAccount()
        val username = account.username
        every { delegate.accountFor(username) } returns Optional.of(account)

        every { cacheOperation.put(account, ttlInSeconds) } just runs

        underTest.accountFor(username)

        verify { delegate.accountFor(username) }
        verify { cacheOperation.put(account, ttlInSeconds) }
    }


    @Test
    fun `when an account is updated`() {
        TODO("Not yet implemented")
    }


}