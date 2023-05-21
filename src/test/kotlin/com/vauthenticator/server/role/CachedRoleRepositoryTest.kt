package com.vauthenticator.server.role

import com.vauthenticator.server.cache.CacheContentConverter
import com.vauthenticator.server.cache.CacheOperation
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@ExtendWith(MockKExtension::class)
class CachedRoleRepositoryTest {

    @MockK
    lateinit var delegate: RoleRepository

    @MockK
    lateinit var cacheOperation: CacheOperation<String, String>

    @MockK
    lateinit var roleCacheContentConverter: CacheContentConverter<List<Role>>

    lateinit var underTest: RoleRepository

    @BeforeEach
    fun setUp() {
        underTest = CachedRoleRepository(roleCacheContentConverter, cacheOperation, delegate)
    }


    @Test
    internal fun `when a role is not found into the cache then it is loaded from database and loaded into the cache`() {
        every { cacheOperation.get("roles") } returns Optional.empty()
        every { delegate.findAll() } returns roles
        every { roleCacheContentConverter.loadableContentIntoCacheFor(roles) } returns "roles-junit-content"
        every { cacheOperation.put("roles", "roles-junit-content") } just runs

        underTest.findAll()

        verify { cacheOperation.get("roles")}
        verify { delegate.findAll() }
        verify { roleCacheContentConverter.loadableContentIntoCacheFor(roles)}
        verify { cacheOperation.put("roles", "roles-junit-content") }
    }

    @Test
    internal fun `when an account is found from the cache `() {
        every { cacheOperation.get("roles") } returns Optional.of("roles-junit-content")
        every { roleCacheContentConverter.getObjectFromCacheContentFor("roles-junit-content") } returns roles

        underTest.findAll()

        verify { cacheOperation.get("roles") }
        verify(exactly = 0) { delegate.findAll() }
        verify { roleCacheContentConverter.getObjectFromCacheContentFor("roles-junit-content") }
        verify(exactly = 0) { cacheOperation.put("roles", "roles-junit-content") }
    }


    @Test
    fun `when an account is updated`() {
        every { cacheOperation.evict("roles") } just runs
        every { delegate.save(defaultRole) } just runs

        underTest.save(defaultRole)

        verify { cacheOperation.evict("roles") }
        verify { delegate.save(defaultRole) }
    }
    @Test
    fun `when an account is deleted`() {
        every { cacheOperation.evict("roles") } just runs
        every { delegate.delete(adminRole.name) } just runs

        underTest.delete(adminRole.name)

        verify { cacheOperation.evict("roles") }
        verify { delegate.delete(adminRole.name) }
    }
}