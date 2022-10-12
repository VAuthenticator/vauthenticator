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
import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientAppFixture.aClientApp
import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientAppFixture.aClientAppId
import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientApplicationRepository
import it.valeriovaudi.vauthenticator.oauth2.clientapp.InsufficientClientApplicationScopeException
import it.valeriovaudi.vauthenticator.oauth2.clientapp.Scope
import it.valeriovaudi.vauthenticator.oauth2.clientapp.Scopes
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

    @MockK
    lateinit var clientApplicationRepository: ClientApplicationRepository

    @BeforeEach
    internal fun setUp() {
        underTest = SendResetPasswordMailChallenge(clientApplicationRepository, accountRepository, ticketFactory, mailSenderService, "https://vauthenticator.com")
    }

    @Test
    internal fun `happy path`() {
        val anAccount = anAccount()
        val clientAppId = aClientAppId()

        every { clientApplicationRepository.findOne(clientAppId) } returns Optional.of(aClientApp(clientAppId).copy(scopes = Scopes(setOf(Scope.RESET_PASSWORD))))
        every { accountRepository.accountFor(anAccount.email) } returns Optional.of(anAccount)
        every { ticketFactory.createTicketFor(anAccount, clientAppId) } returns VerificationTicket("A_TICKET")
        every { mailSenderService.sendFor(anAccount, mapOf("resetPasswordLink" to "https://vauthenticator.com/reset-password/A_TICKET")) } just runs

        underTest.sendResetPasswordMail(anAccount.email, clientAppId)
    }

    @Test
    internal fun `when the client app does not have enought scopes`() {
        val anAccount = anAccount()
        val clientAppId = aClientAppId()

        every { clientApplicationRepository.findOne(clientAppId) } returns Optional.of(aClientApp(clientAppId))

        assertThrows(InsufficientClientApplicationScopeException::class.java) {
            underTest.sendResetPasswordMail(anAccount.email, clientAppId)
        }
    }
}