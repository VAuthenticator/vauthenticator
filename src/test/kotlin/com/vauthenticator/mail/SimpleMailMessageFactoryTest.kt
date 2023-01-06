package com.vauthenticator.mail

import com.vauthenticator.account.AccountTestFixture.anAccount
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class SimpleMailMessageFactoryTest {

    private val underTest = SimpleMailMessageFactory("from", "subject", MailType.WELCOME)

    @Test
    internal fun `make a new mail message`() {
        val account = anAccount()
        val actual = underTest.makeMailMessageFor(account, mapOf("key" to "value"))

        val expected = MailMessage(
                "email@domain.com", "from", "subject", MailType.WELCOME,
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