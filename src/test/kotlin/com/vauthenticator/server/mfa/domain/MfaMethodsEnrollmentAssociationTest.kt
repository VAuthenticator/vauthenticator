package com.vauthenticator.server.mfa.domain

import com.vauthenticator.server.keys.Kid
import com.vauthenticator.server.oauth2.clientapp.domain.ClientAppId
import com.vauthenticator.server.support.AccountTestFixture
import com.vauthenticator.server.support.TicketFixture
import com.vauthenticator.server.ticket.InvalidTicketException
import com.vauthenticator.server.ticket.Ticket
import com.vauthenticator.server.ticket.TicketId
import com.vauthenticator.server.ticket.TicketRepository
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

private const val RAW_TICKET = "A_TICKET"
private const val CODE = "CODE"


@ExtendWith(MockKExtension::class)
class MfaMethodsEnrollmentAssociationTest {
    private val account = AccountTestFixture.anAccount()
    private val email = account.email
    private val userName = email

    private val mfaAccountMethod = MfaAccountMethod(
        email,
        MfaDeviceId("A_MFA_DEVICE_ID"),
        Kid("A_KID"),
        MfaMethod.EMAIL_MFA_METHOD,
        email,
        true
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
                context = TicketFixture.ticketContext(userName, Ticket.MFA_SELF_ASSOCIATION_CONTEXT_VALUE)
            )
        every { ticketRepository.loadFor(ticketId) } returns Optional.of(ticketWithAutoAssociationFeatureEnabled)
        every {
            mfaAccountMethodsRepository.save(
                userName,
                ticket.context.mfaMethod(),
                ticket.context.mfaChannel(),
                true
            )
        } returns mfaAccountMethod
        every { ticketRepository.delete(ticketWithAutoAssociationFeatureEnabled.ticketId) } just runs

        underTest.associate(RAW_TICKET)

        verify { ticketRepository.loadFor(ticketId) }
        verify {
            mfaAccountMethodsRepository.save(
                userName,
                ticket.context.mfaMethod(),
                ticket.context.mfaChannel(),
                true
            )
        }
        verify { ticketRepository.delete(ticketWithAutoAssociationFeatureEnabled.ticketId) }
    }

    @Test
    fun `when mfa is associated`() {
        every { ticketRepository.loadFor(ticketId) } returns Optional.of(ticket)
        every {
            otpMfaVerifier.verifyMfaChallengeToBeAssociatedFor(
                userName,
                ticket.context.mfaMethod(),
                ticket.context.mfaChannel(),
                MfaChallenge(CODE)
            )
        } just runs
        every {
            mfaAccountMethodsRepository.save(
                userName,
                ticket.context.mfaMethod(),
                ticket.context.mfaChannel(),
                true
            )
        } returns mfaAccountMethod
        every { ticketRepository.delete(ticket.ticketId) } just runs


        underTest.associate(RAW_TICKET, CODE)

        verify { ticketRepository.loadFor(ticketId) }
        verify {
            mfaAccountMethodsRepository.save(
                userName,
                ticket.context.mfaMethod(),
                ticket.context.mfaChannel(),
                true
            )
        }
        verify {
            otpMfaVerifier.verifyMfaChallengeToBeAssociatedFor(
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
        every { ticketRepository.loadFor(ticketId) } returns Optional.empty()

        Assertions.assertThrows(InvalidTicketException::class.java) { underTest.associate(RAW_TICKET, CODE) }

        verify { ticketRepository.loadFor(ticketId) }
    }
}