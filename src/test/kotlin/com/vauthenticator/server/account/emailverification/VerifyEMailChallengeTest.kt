package com.vauthenticator.server.account.emailverification

import com.vauthenticator.server.account.repository.AccountRepository
import com.vauthenticator.server.mfa.domain.MfaMethod
import com.vauthenticator.server.mfa.domain.MfaMethodsEnrollmentAssociation
import com.vauthenticator.server.oauth2.clientapp.ClientAppId
import com.vauthenticator.server.support.AccountTestFixture
import com.vauthenticator.server.support.TicketFixture
import com.vauthenticator.server.ticket.InvalidTicketException
import com.vauthenticator.server.ticket.TicketRepository
import com.vauthenticator.server.ticket.VerificationTicket
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@ExtendWith(MockKExtension::class)
internal class VerifyEMailChallengeTest {

    @MockK
    lateinit var accountRepository: AccountRepository

    @MockK
    lateinit var ticketRepository: TicketRepository

    @MockK
    lateinit var mfaMethodsEnrollmentAssociation: MfaMethodsEnrollmentAssociation


    private lateinit var underTest: VerifyEMailChallenge

    @BeforeEach
    fun setup() {
        underTest = VerifyEMailChallenge(
            ticketRepository,
            accountRepository,
            mfaMethodsEnrollmentAssociation
        )
    }

    @Test
    internal fun `happy path`() {
        val account = AccountTestFixture.anAccount()
        val enabledAccount = account.copy(accountNonLocked = true, enabled = true, emailVerified = true)
        val verificationTicket = VerificationTicket("A_TICKET")

        every { ticketRepository.loadFor(verificationTicket) } returns Optional.of(
            TicketFixture.ticketFor(
                verificationTicket.content,
                account.email,
                ClientAppId.empty().content
            )
        )
        every { mfaMethodsEnrollmentAssociation.associate("A_TICKET", MfaMethod.EMAIL_MFA_METHOD) } just runs
        every { accountRepository.accountFor(account.email) } returns Optional.of(account)
        every { accountRepository.save(enabledAccount) } just runs
        every { ticketRepository.delete(verificationTicket) } just runs

        underTest.verifyMail("A_TICKET")
        verify(exactly = 1) { mfaMethodsEnrollmentAssociation.associate("A_TICKET", MfaMethod.EMAIL_MFA_METHOD) }
    }

    @Test
    internal fun `when the account does not exist`() {
        val account = AccountTestFixture.anAccount()
        val verificationTicket = VerificationTicket("A_TICKET")

        every { ticketRepository.loadFor(verificationTicket) } returns Optional.of(
            TicketFixture.ticketFor(
                verificationTicket.content,
                account.email,
                ClientAppId.empty().content
            )
        )
        every { accountRepository.accountFor(account.email) } returns Optional.empty()
        every { mfaMethodsEnrollmentAssociation.associate("A_TICKET", MfaMethod.EMAIL_MFA_METHOD) } just runs

        assertThrows(InvalidTicketException::class.java) { underTest.verifyMail("A_TICKET") }
    }

    @Test
    internal fun `when the ticket does not exist`() {
        val verificationTicket = VerificationTicket("A_TICKET")

        every { ticketRepository.loadFor(verificationTicket) } returns Optional.empty()

        assertThrows(InvalidTicketException::class.java) { underTest.verifyMail("A_TICKET") }
    }

}