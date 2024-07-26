package com.vauthenticator.server.ticket

import com.vauthenticator.server.keys.Kid
import com.vauthenticator.server.mfa.domain.MfaAccountMethod
import com.vauthenticator.server.mfa.domain.MfaChallenge
import com.vauthenticator.server.mfa.domain.MfaMethod.EMAIL_MFA_METHOD
import com.vauthenticator.server.mfa.domain.MfaMethodsEnrollmentAssociation
import com.vauthenticator.server.mfa.domain.OtpMfaVerifier
import com.vauthenticator.server.mfa.repository.MfaAccountMethodsRepository
import com.vauthenticator.server.oauth2.clientapp.ClientAppId
import com.vauthenticator.server.support.AccountTestFixture.anAccount
import com.vauthenticator.server.support.TicketFixture
import com.vauthenticator.server.ticket.Ticket.Companion.MFA_SELF_ASSOCIATION_CONTEXT_KEY
import com.vauthenticator.server.ticket.Ticket.Companion.MFA_SELF_ASSOCIATION_CONTEXT_VALUE
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
import java.util.Optional.empty
import java.util.Optional.of

private const val RAW_TICKET = "A_TICKET"
private const val CODE = "CODE"


@ExtendWith(MockKExtension::class)
class MfaMethodsEnrollmentAssociationTest {
    private val account = anAccount()
    private val email = account.email
    private val userName = email

    private val mfaAccountMethod = MfaAccountMethod(
        email,
        Kid(""),
        EMAIL_MFA_METHOD,
        email,
    )
    private val ticket = TicketFixture.ticketFor(
        RAW_TICKET,
        userName,
        ClientAppId.empty().content,
    )
    private val ticketId = TicketId(RAW_TICKET)

    @MockK
    lateinit var ticketRepository: TicketRepository

    @MockK
    lateinit var mfaAccountMethodsRepository: MfaAccountMethodsRepository

    @MockK
    lateinit var otpMfaVerifier: OtpMfaVerifier

    lateinit var underTest: MfaMethodsEnrollmentAssociation

    @BeforeEach
    fun setUp() {
        underTest = MfaMethodsEnrollmentAssociation(ticketRepository, mfaAccountMethodsRepository, otpMfaVerifier)
    }

    @Test
    fun `when mfa is associated with auto association feature enabled`() {
        val ticketWithAutoAssociationFeatureEnabled =
            ticket.copy(
                context = TicketContext(
                    mapOf(MFA_SELF_ASSOCIATION_CONTEXT_KEY to MFA_SELF_ASSOCIATION_CONTEXT_VALUE)
                )
            )
        every { ticketRepository.loadFor(ticketId) } returns of(ticketWithAutoAssociationFeatureEnabled)
        every { ticketRepository.delete(ticketWithAutoAssociationFeatureEnabled.ticketId) } just runs

        underTest.associate(RAW_TICKET)

        verify { ticketRepository.loadFor(ticketId) }
        verify { ticketRepository.delete(ticketWithAutoAssociationFeatureEnabled.ticketId) }
    }

    @Test
    fun `when mfa is associated`() {
        every { ticketRepository.loadFor(ticketId) } returns of(ticket)
        every {
            otpMfaVerifier.verifyMfaChallengeFor(
                userName,
                ticket.context.mfaMethod(),
                ticket.context.mfaChannel(),
                MfaChallenge(CODE)
            )
        } just runs
        every { ticketRepository.delete(ticket.ticketId) } just runs


        underTest.associate(RAW_TICKET, CODE)

        verify { ticketRepository.loadFor(ticketId) }
        verify {
            otpMfaVerifier.verifyMfaChallengeFor(
                userName,
                ticket.context.mfaMethod(),
                ticket.context.mfaChannel(),
                MfaChallenge(CODE)
            )
        }
        verify { ticketRepository.delete(ticket.ticketId) }
    }

    @Test
    fun `when a ticket is expired`() {
        every { ticketRepository.loadFor(ticketId) } returns empty()

        assertThrows(InvalidTicketException::class.java) { underTest.associate(RAW_TICKET, CODE) }

        verify { ticketRepository.loadFor(ticketId) }
    }
}