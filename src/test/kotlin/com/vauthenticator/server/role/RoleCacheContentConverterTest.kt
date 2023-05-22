package com.vauthenticator.server.role

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.vauthenticator.server.support.JsonUtils.prettifyInOneLineJsonFrom
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

private const val testableResource = "roles/rolesSample.json"

private val roleList = listOf(Role("ROLE_1", "ROLE_1"), Role("ROLE_2", "ROLE_2"))
private val rawValue = prettifyInOneLineJsonFrom(testableResource)

class RoleCacheContentConverterTest {

    private val underTest = RoleCacheContentConverter(jacksonObjectMapper())

    @Test
    fun `when a string value from the cache is deserialized`() {
        val actual = underTest.getObjectFromCacheContentFor(rawValue)
        assertEquals(roleList, actual)
    }

    @Test
    fun `when a role object is serialized as cached value`() {
        val actual = underTest.loadableContentIntoCacheFor(roleList)
        assertEquals(rawValue, actual)
    }
}