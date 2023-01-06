package com.vauthenticator.password

import com.vauthenticator.password.*
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class PasswordPolicyTest {

    @MockK
    private lateinit var firstPolicy: PasswordPolicy

    @MockK
    private lateinit var secondPolicy: PasswordPolicy

    @Test
    internal fun `when a password has no special character`() {
        val underTest = SpecialCharacterPasswordPolicy(2)
        assertThrows(PasswordPolicyViolation::class.java) { underTest.accept("aPassword") }
    }
   @Test
    internal fun `when a password has enoguht special  character`() {
        val underTest = SpecialCharacterPasswordPolicy(2)
        underTest.accept("aPa!%ssword")
    }

    @Test
    internal fun `when a password has not allowed special character`() {
        val underTest = MinimumCharacterPasswordPolicy(8)
        assertThrows(PasswordPolicyViolation::class.java) { underTest.accept("1245789") }
    }

    @Test
    internal fun `when a set of password policies are invoked`() {
        val underTest = CompositePasswordPolicy(setOf(firstPolicy, secondPolicy))

        every { firstPolicy.accept("1245789") } just runs
        every { secondPolicy.accept("1245789") } throws PasswordPolicyViolation("")

        assertThrows(PasswordPolicyViolation::class.java) { underTest.accept("1245789") }

    }
}