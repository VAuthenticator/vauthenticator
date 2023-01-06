package com.vauthenticator.account.resetpassword

import com.vauthenticator.account.AccountTestFixture.anAccount
import com.vauthenticator.account.repository.AccountRepository
import com.vauthenticator.account.tiket.VerificationTicket
import com.vauthenticator.account.tiket.VerificationTicketFactory
import com.vauthenticator.mail.MailSenderService
import com.vauthenticator.oauth2.clientapp.ClientAppFixture.aClientApp
import com.vauthenticator.oauth2.clientapp.ClientAppFixture.aClientAppId
import com.vauthenticator.oauth2.clientapp.ClientApplicationRepository
import com.vauthenticator.oauth2.clientapp.InsufficientClientApplicationScopeException
import com.vauthenticator.oauth2.clientapp.Scope
import com.vauthenticator.oauth2.clientapp.Scopes
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
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

        every { clientApplicationRepository.findOne(clientAppId) } returns Optional.of(aClientApp(clientAppId).copy(scopes = Scopes(setOf(
            Scope.RESET_PASSWORD))
        ))
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