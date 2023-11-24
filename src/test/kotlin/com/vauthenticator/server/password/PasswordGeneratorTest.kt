package com.vauthenticator.server.password

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

private const val STRONG_PASSWORD_LENGTH = 20

class PasswordGeneratorTest {

    @Test
    fun name() {
        val passwordGenerator = PasswordGenerator(PasswordCriteria(10, 5, 5))

        val actual = passwordGenerator.generate()
        println(actual)
        Assertions.assertTrue(actual.length == STRONG_PASSWORD_LENGTH)
    }

    @Test
    fun name2() {
        for (index in 33..126) {
            println(Char(index))
        }
    }
}
