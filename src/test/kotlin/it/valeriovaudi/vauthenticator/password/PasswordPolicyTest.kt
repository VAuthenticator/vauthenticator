package it.valeriovaudi.vauthenticator.password

import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

internal class PasswordPolicyTest {

    @Test
    internal fun `when a password has no special character`() {
        val underTest = SpecialCharacterPasswordPolicy()
        assertThrows(PasswordPolicyViolation::class.java) { underTest.accept("aPassword") }
    }

    @Test
    internal fun `when a password has not allowed special character`() {
        val underTest = MinimumCharacterPasswordPolicy(8)
        assertThrows(PasswordPolicyViolation::class.java) { underTest.accept("1245789") }
    }

}