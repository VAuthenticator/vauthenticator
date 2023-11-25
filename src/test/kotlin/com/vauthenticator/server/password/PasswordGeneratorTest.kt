package com.vauthenticator.server.password

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

private const val STRONG_PASSWORD_LENGTH = 20

class PasswordGeneratorTest {

    private val uut = PasswordGenerator(PasswordGeneratorCriteria(5, 5, 5, 5))


    @Test
    fun name() {

        val actual = uut.generate()
        assertEquals(STRONG_PASSWORD_LENGTH, actual.length)
        assertTrue(actual.length == STRONG_PASSWORD_LENGTH)
    }

}