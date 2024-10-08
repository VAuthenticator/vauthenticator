package com.vauthenticator.server.communication.email

import com.vauthenticator.server.communication.domain.EMailMessage
import com.vauthenticator.server.communication.domain.EMailType
import com.vauthenticator.server.communication.domain.SimpleEMailMessageFactory
import com.vauthenticator.server.support.AccountTestFixture.anAccount
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class SimpleEMailMessageFactoryTest {

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

    @Test
    internal fun `make a new mail message when the sender mail has changed`() {
        val account = anAccount()
        val actual = underTest.makeMailMessageFor(account, mapOf("key" to "value", "email" to "new-accountmail@email.com"))

        val expected = EMailMessage(
                "new-accountmail@email.com", "from", "subject", EMailType.WELCOME,
                mapOf(
                        "enabled" to account.enabled,
                        "username" to account.username,
                        "authorities" to account.authorities,
                        "email" to "new-accountmail@email.com",
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