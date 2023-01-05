package it.valeriovaudi.vauthenticator.oauth2.clientapp

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import it.valeriovaudi.vauthenticator.cache.CacheContentConverter
import it.valeriovaudi.vauthenticator.cache.CacheOperation
import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientAppFixture.aClientApp
import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientAppFixture.aClientAppId
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@ExtendWith(MockKExtension::class)
class CachedClientApplicationRepositoryTest {

    @MockK
    lateinit var delegate: ClientApplicationRepository

    @MockK
    lateinit var cacheOperation: CacheOperation<String, String>

    @MockK
    lateinit var cacheContentConverter: CacheContentConverter<ClientApplication>

    lateinit var underTest: ClientApplicationRepository

    @BeforeEach
    fun setUp() {
        underTest = CachedClientApplicationRepository(cacheContentConverter, cacheOperation, delegate)
    }


    @Test
    internal fun `when a client app is not found into the cache then it is loaded from database and loaded into the cache`() {
        val clientAppId = aClientAppId()
        val clientApplication = aClientApp(clientAppId)

        every { cacheOperation.get(clientAppId.content) } returns Optional.empty()
        every { delegate.findOne(clientAppId) } returns Optional.of(clientApplication)
        every { cacheContentConverter.loadableContentIntoCacheFor(clientApplication) } returns "content"
        every { cacheOperation.put(clientAppId.content, "content") } just runs

        underTest.findOne(clientAppId)

        verify { cacheOperation.get(clientAppId.content) }
        verify { delegate.findOne(clientAppId) }
        verify { cacheContentConverter.loadableContentIntoCacheFor(clientApplication) }
        verify { cacheOperation.put(clientAppId.content, "content") }
    }
    /*

        @Test
        internal fun `when an account is found from the cache `() {
            val account = AccountTestFixture.anAccount()
            val username = account.username

            every { accountCacheContentConverter.getObjectFromCacheContentFor("account") } returns account
            every { cacheOperation.get(account.email) } returns Optional.of("account")

            underTest.accountFor(username)

            verify { cacheOperation.get(account.email) }
            verify(exactly = 0) { delegate.accountFor(username) }
            verify { accountCacheContentConverter.getObjectFromCacheContentFor("account") }
            verify(exactly = 0) { cacheOperation.put(username, "account") }
        }


        @Test
        fun `when an account is updated`() {
            val account = AccountTestFixture.anAccount()
            every { cacheOperation.evict(account.email) } just runs
            every { delegate.save(account) } just runs

            underTest.save(account)

            verify { cacheOperation.evict(account.email) }
            verify { delegate.save(account) }
        }
    */

}