package it.valeriovaudi.vauthenticator.account.resetpassword

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import it.valeriovaudi.vauthenticator.account.AccountTestFixture.anAccount
import it.valeriovaudi.vauthenticator.account.repository.AccountRepository
import it.valeriovaudi.vauthenticator.account.tiket.VerificationTicket
import it.valeriovaudi.vauthenticator.account.tiket.VerificationTicketFactory
import it.valeriovaudi.vauthenticator.mail.MailSenderService
import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientAppId.Companion.empty
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@ExtendWith(MockKExtension::class)
internal class SendResetPasswordMailChallengeTest {

    lateinit var underTest: SendResetPasswordMailChallenge

    @MockK
    lateinit var accountRepository: AccountRepository

    @MockK
    lateinit var ticketFactory: VerificationTicketFactory

    @MockK
    lateinit var mailSenderService: MailSenderService

    @BeforeEach
    internal fun setUp() {
        underTest = SendResetPasswordMailChallenge(accountRepository, ticketFactory, mailSenderService)
    }

    @Test
    internal fun `happy path`() {
        val anAccount = anAccount()

        every { accountRepository.accountFor(anAccount.email) } returns Optional.of(anAccount)
        every { ticketFactory.createTicketFor(anAccount, empty()) } returns VerificationTicket("A_TICKET")
        every { mailSenderService.sendFor(anAccount, mapOf("resetPasswordTicket" to "A_TICKET")) } just runs

        underTest.sendResetPasswordMail(anAccount.email)
    }
}