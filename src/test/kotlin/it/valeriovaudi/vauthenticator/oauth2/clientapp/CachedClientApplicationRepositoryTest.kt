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

    private val clientAppId = aClientAppId()
    private val clientApplication = aClientApp(clientAppId)

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


    @Test
    internal fun `when a client application is found from the cache `() {
        every { cacheOperation.get(clientAppId.content) } returns Optional.of("content")
        every { cacheContentConverter.getObjectFromCacheContentFor("content") } returns clientApplication

        underTest.findOne(clientAppId)

        verify { cacheOperation.get(clientAppId.content) }
        verify(exactly = 0) { delegate.findOne(clientAppId) }
        verify { cacheContentConverter.getObjectFromCacheContentFor("content") }
        verify(exactly = 0) { cacheOperation.put(clientAppId.content, "content") }
    }

    @Test
    fun `when an account is updated`() {
        every { cacheOperation.evict(clientAppId.content) } just runs
        every { delegate.save(clientApplication) } just runs

        underTest.save(clientApplication)

        verify { cacheOperation.evict(clientAppId.content) }
        verify { delegate.save(clientApplication) }
    }


}