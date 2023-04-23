package com.vauthenticator.server.account.resetpassword

import com.vauthenticator.server.account.AccountTestFixture.anAccount
import com.vauthenticator.server.account.repository.AccountRepository
import com.vauthenticator.server.account.tiket.VerificationTicket
import com.vauthenticator.server.account.tiket.VerificationTicketFactory
import com.vauthenticator.server.clientapp.ClientAppFixture.aClientApp
import com.vauthenticator.server.clientapp.ClientAppFixture.aClientAppId
import com.vauthenticator.server.mail.MailSenderService
import com.vauthenticator.server.oauth2.clientapp.ClientApplicationRepository
import com.vauthenticator.server.oauth2.clientapp.InsufficientClientApplicationScopeException
import com.vauthenticator.server.oauth2.clientapp.Scope
import com.vauthenticator.server.oauth2.clientapp.Scopes
import com.vauthenticator.server.support.SecurityFixture.principalFor
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
        underTest = SendResetPasswordMailChallenge(
            clientApplicationRepository,
            accountRepository,
            ticketFactory,
            mailSenderService,
            "https://vauthenticator.com"
        )
    }

    @Test
    internal fun `when the reset password challenge is sent for anonymous call`() {
        val anAccount = anAccount()
        val clientAppId = aClientAppId()

        every { clientApplicationRepository.findOne(clientAppId) } returns Optional.of(
            aClientApp(clientAppId)
                .copy(scopes = Scopes(setOf(Scope.RESET_PASSWORD)))
        )
        every { accountRepository.accountFor(anAccount.email) } returns Optional.of(anAccount)
        every { ticketFactory.createTicketFor(anAccount, clientAppId) } returns VerificationTicket("A_TICKET")
        every {
            mailSenderService.sendFor(
                anAccount,
                mapOf("resetPasswordLink" to "https://vauthenticator.com/reset-password/A_TICKET")
            )
        } just runs

        underTest.sendResetPasswordMail(anAccount.email, clientAppId)
    }

    @Test
    internal fun `when the reset password challenge is sent for autheticated call`() {
        val anAccount = anAccount()
        val clientAppId = aClientAppId()

        every { accountRepository.accountFor(anAccount.email) } returns Optional.of(anAccount)
        every { ticketFactory.createTicketFor(anAccount, clientAppId) } returns VerificationTicket("A_TICKET")
        every {
            mailSenderService.sendFor(
                anAccount,
                mapOf("resetPasswordLink" to "https://vauthenticator.com/reset-password/A_TICKET")
            )
        } just runs

        underTest.sendResetPasswordMail(
            anAccount.email,
            principalFor(clientAppId.content, anAccount.email, emptyList(), listOf(Scope.RESET_PASSWORD.content))
        )
    }

    @Test
    internal fun `when the client app does not have enough scopes`() {
        val anAccount = anAccount()
        val clientAppId = aClientAppId()

        every { clientApplicationRepository.findOne(clientAppId) } returns Optional.of(aClientApp(clientAppId))

        assertThrows(InsufficientClientApplicationScopeException::class.java) {
            underTest.sendResetPasswordMail(anAccount.email, clientAppId)
        }
    }

    @Test
    internal fun `when the token does not have enough scopes`() {
        val anAccount = anAccount()
        val clientAppId = aClientAppId()

        assertThrows(InsufficientClientApplicationScopeException::class.java) {
            underTest.sendResetPasswordMail(anAccount.email, principalFor(clientAppId.content, anAccount.email))
        }
    }
}