package it.valeriovaudi.vauthenticator.account.repository

import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import it.valeriovaudi.vauthenticator.cache.CacheOperation
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import java.time.Duration
import java.util.*

@ExtendWith(MockKExtension::class)
internal class CachedAccountRepositoryTest {
    private val ttlInSeconds = Duration.ofSeconds(200)

    @MockK
    lateinit var delegate: AccountRepository

    @MockK
    lateinit var cacheOperation: CacheOperation<String, String>

    lateinit var underTest: AccountRepository

    @BeforeEach
    fun setUp() {
        underTest = CachedAccountRepository(ObjectMapper(), cacheOperation, delegate)
    }
    /*

        @Test
        internal fun `when an account is not found into the cache then it is loaded from database and loaded into the cache`() {
            val account = anAccount()
            val username = account.username
            every { cacheOperation.get(account.email) } returns Optional.empty()
            every { delegate.accountFor(username) } returns Optional.of(account)
            every { cacheOperation.put(username, account) } just runs

            underTest.accountFor(username)

            verify { cacheOperation.get(account.email) }
            verify { delegate.accountFor(username) }
            verify { cacheOperation.put(username, account) }
        }

        @Test
        internal fun `when an account is found from the cache `() {
            val account = anAccount()
            val username = account.username
            every { cacheOperation.get(account.email) } returns Optional.of(account)

            underTest.accountFor(username)

            verify { cacheOperation.get(account.email) }
            verify(exactly = 0) { delegate.accountFor(username) }
            verify(exactly = 0) { cacheOperation.put(username, account) }
        }


        @Test
        fun `when an account is updated`() {
            val account = anAccount()
            every { cacheOperation.evict(account.email) } just runs
            every { delegate.save(account) } just runs

            underTest.save(account)

            verify { cacheOperation.evict(account.email) }
            verify { delegate.save(account) }
        }
    */

}