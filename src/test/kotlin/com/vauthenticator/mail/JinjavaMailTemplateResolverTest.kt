package com.vauthenticator.mail

import com.hubspot.jinjava.Jinjava
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class JinjavaMailTemplateResolverTest {

    @Test
    internal fun `happy path`() {
        val jinjavaMailTemplateResolver = JinjavaMailTemplateResolver(Jinjava())

        val expected = "Hello Jho!"
        val actual = jinjavaMailTemplateResolver.compile("Hello {{ name }}!", mapOf("name" to "Jho"))

        assertEquals(expected, actual)
    }
}