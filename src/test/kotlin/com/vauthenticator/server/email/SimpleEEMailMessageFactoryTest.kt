package com.vauthenticator.server.email

import com.vauthenticator.server.support.AccountTestFixture.anAccount
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class SimpleEEMailMessageFactoryTest {

    private val underTest = SimpleEMailMessageFactory("from", "subject", EMailType.WELCOME)

    @Test
    internal fun `make a new mail message`() {
        val account = anAccount()
        val actual = underTest.makeMailMessageFor(account, mapOf("key" to "value"))

        val expected = EMailMessage(
                "email@domain.com", "from", "subject", EMailType.WELCOME,
                mapOf(
                        "enabled" to account.enabled,
                        "username" to account.username,
                        "authorities" to account.authorities,
                        "email" to account.email,
                        "firstName" to account.firstName,
                        "lastName" to account.lastName,
                        "birthDate" to "",
                        "phone" to "",
                        "key" to "value"
                )
        )

        assertEquals(expected, actual)
    }

}