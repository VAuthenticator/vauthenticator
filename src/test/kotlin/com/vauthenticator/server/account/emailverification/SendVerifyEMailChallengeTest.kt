package com.vauthenticator.server.account.emailverification

import com.vauthenticator.server.account.AccountNotFoundException
import com.vauthenticator.server.account.repository.AccountRepository
import com.vauthenticator.server.account.ticket.VerificationTicket
import com.vauthenticator.server.account.ticket.VerificationTicketFactory
import com.vauthenticator.server.clientapp.A_CLIENT_APP_ID
import com.vauthenticator.server.clientapp.ClientAppFixture.aClientApp
import com.vauthenticator.server.email.MailSenderService
import com.vauthenticator.server.oauth2.clientapp.ClientAppId
import com.vauthenticator.server.oauth2.clientapp.ClientApplicationRepository
import com.vauthenticator.server.oauth2.clientapp.Scope
import com.vauthenticator.server.oauth2.clientapp.Scopes
import com.vauthenticator.server.support.AccountTestFixture.anAccount
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@ExtendWith(MockKExtension::class)
internal class SendVerifyEMailChallengeTest {

    @MockK
    lateinit var clientAccountRepository: ClientApplicationRepository

    @MockK
    lateinit var accountRepository: AccountRepository

    @MockK
    lateinit var verificationTicketFactory: VerificationTicketFactory

    @MockK
    lateinit var mailVerificationMailSender: MailSenderService

    private lateinit var underTest: SendVerifyEMailChallenge

    @BeforeEach
    fun setup() {
        underTest = SendVerifyEMailChallenge(
            accountRepository,
            verificationTicketFactory,
            mailVerificationMailSender,
            "https://vauthenticator.com"
        )
    }

    @Test
    internal fun `happy path`() {
        val account = anAccount()
        val verificationTicket = VerificationTicket("A_TICKET")
        val requestContext = mapOf("verificationEMailLink" to "https://vauthenticator.com/email-verify/A_TICKET")


        every { accountRepository.accountFor(account.email) } returns Optional.of(account)
        every { verificationTicketFactory.createTicketFor(account, ClientAppId.empty() ) } returns verificationTicket
        every { mailVerificationMailSender.sendFor(account, requestContext) } just runs

        underTest.sendVerifyMail(account.email)

        verify { mailVerificationMailSender.sendFor(account, requestContext) }
    }

    @Test
    internal fun `when account does not exist`() {
        val email = "anemail@test.com"
        val clientAppId = ClientAppId(A_CLIENT_APP_ID)
        val clientApplication = aClientApp(clientAppId).copy(scopes = Scopes.from(Scope.MAIL_VERIFY))

        every { clientAccountRepository.findOne(clientAppId) } returns Optional.of(clientApplication)
        every { accountRepository.accountFor(email) } returns Optional.empty()

        Assertions.assertThrows(AccountNotFoundException::class.java) {
            underTest.sendVerifyMail(email)
        }
    }

}