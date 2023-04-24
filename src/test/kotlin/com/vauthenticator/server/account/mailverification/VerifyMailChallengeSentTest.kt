package com.vauthenticator.server.account.mailverification

import com.vauthenticator.server.account.repository.AccountRepository
import com.vauthenticator.server.account.tiket.InvalidTicketException
import com.vauthenticator.server.account.tiket.TicketRepository
import com.vauthenticator.server.account.tiket.VerificationTicket
import com.vauthenticator.server.mfa.MfaMethod
import com.vauthenticator.server.mfa.MfaMethodsEnrolmentAssociation
import com.vauthenticator.server.oauth2.clientapp.*
import com.vauthenticator.server.support.TicketFixture
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@ExtendWith(MockKExtension::class)
internal class VerifyMailChallengeSentTest {

    @MockK
    lateinit var accountRepository: AccountRepository

    @MockK
    lateinit var ticketRepository: TicketRepository

    @MockK
    lateinit var mfaMethodsEnrolmentAssociation: MfaMethodsEnrolmentAssociation

    private lateinit var underTest: VerifyMailChallengeSent

    @BeforeEach
    fun setup() {
        underTest = VerifyMailChallengeSent(
            accountRepository,
            ticketRepository,
            mfaMethodsEnrolmentAssociation
        )
    }

    @Test
    internal fun `happy path`() {
        val account = com.vauthenticator.server.account.AccountTestFixture.anAccount()
        val enabledAccount = account.copy(accountNonLocked = true, enabled = true, emailVerified = true)
        val verificationTicket = VerificationTicket("A_TICKET")

        every { ticketRepository.loadFor(verificationTicket) } returns Optional.of(
            TicketFixture.ticketFor(
                verificationTicket.content,
                account.email,
                ClientAppId.empty().content
            )
        )
        every { mfaMethodsEnrolmentAssociation.associate(enabledAccount, MfaMethod.EMAIL_MFA_METHOD) } just runs
        every { accountRepository.accountFor(account.email) } returns Optional.of(account)
        every { accountRepository.save(enabledAccount) } just runs
        every { ticketRepository.delete(verificationTicket) } just runs

        underTest.verifyMail("A_TICKET")
        verify(exactly = 1) { mfaMethodsEnrolmentAssociation.associate(enabledAccount, MfaMethod.EMAIL_MFA_METHOD) }
    }

    @Test
    internal fun `when the account does not exist`() {
        val account = com.vauthenticator.server.account.AccountTestFixture.anAccount()
        val verificationTicket = VerificationTicket("A_TICKET")

        every { ticketRepository.loadFor(verificationTicket) } returns Optional.of(
            TicketFixture.ticketFor(
                verificationTicket.content,
                account.email,
                ClientAppId.empty().content
            )
        )
        every { accountRepository.accountFor(account.email) } returns Optional.empty()

        assertThrows(InvalidTicketException::class.java) { underTest.verifyMail("A_TICKET") }
    }

    @Test
    internal fun `when the ticket does not exist`() {
        val verificationTicket = VerificationTicket("A_TICKET")

        every { ticketRepository.loadFor(verificationTicket) } returns Optional.empty()

        assertThrows(InvalidTicketException::class.java) { underTest.verifyMail("A_TICKET") }
    }

}