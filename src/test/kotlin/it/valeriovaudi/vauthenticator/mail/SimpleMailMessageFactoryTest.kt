package it.valeriovaudi.vauthenticator.mail

import it.valeriovaudi.vauthenticator.account.AccountTestFixture.anAccount
import org.junit.jupiter.api.Test

internal class SimpleMailMessageFactoryTest {

    @Test
    internal fun `make a new mail message`() {
        val underTest = SimpleMailMessageFactory("from", "subject", MailType.WELCOME)
        underTest.makeMailMessageFor(anAccount(), emptyMap())

        TODO()
    }
}