package com.vauthenticator.server.password.resetpassword

import com.vauthenticator.server.account.AccountTestFixture.anAccount
import com.vauthenticator.server.account.repository.AccountRepository
import com.vauthenticator.server.account.ticket.VerificationTicket
import com.vauthenticator.server.account.ticket.VerificationTicketFactory
import com.vauthenticator.server.mail.MailSenderService
import com.vauthenticator.server.oauth2.clientapp.ClientAppId
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
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
        underTest = SendResetPasswordMailChallenge(
            accountRepository,
            ticketFactory,
            mailSenderService,
            "https://vauthenticator.com"
        )
    }

    @Test
    internal fun `when the reset password challenge is sent`() {
        val anAccount = anAccount()

        every { accountRepository.accountFor(anAccount.email) } returns Optional.of(anAccount)
        every { ticketFactory.createTicketFor(anAccount, ClientAppId.empty()) } returns VerificationTicket("A_TICKET")
        every {
            mailSenderService.sendFor(
                anAccount,
                mapOf("resetPasswordLink" to "https://vauthenticator.com/reset-password/A_TICKET")
            )
        } just runs

        underTest.sendResetPasswordMailFor(anAccount.email)
    }

}