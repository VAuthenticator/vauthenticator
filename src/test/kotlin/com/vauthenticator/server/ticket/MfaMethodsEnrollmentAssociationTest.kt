package com.vauthenticator.server.ticket

import com.vauthenticator.server.keys.Kid
import com.vauthenticator.server.mfa.domain.MfaAccountMethod
import com.vauthenticator.server.mfa.domain.MfaMethod
import com.vauthenticator.server.mfa.domain.MfaMethodsEnrollmentAssociation
import com.vauthenticator.server.mfa.repository.MfaAccountMethodsRepository
import com.vauthenticator.server.oauth2.clientapp.ClientAppId
import com.vauthenticator.server.support.AccountTestFixture.anAccount
import com.vauthenticator.server.support.TicketFixture
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

private const val RAW_TICKET = "A_TICKET"

@ExtendWith(MockKExtension::class)
class MfaMethodsEnrollmentAssociationTest {
    val account = anAccount()
    val email = account.email

    private val mfaAccountMethod = MfaAccountMethod(
        email,
        Kid(""),
        MfaMethod.EMAIL_MFA_METHOD
    )
    private val ticket = TicketFixture.ticketFor(
        RAW_TICKET,
        account.email,
        ClientAppId.empty().content
    )
    private val verificationTicket = VerificationTicket(RAW_TICKET)

    @MockK
    lateinit var ticketRepository: TicketRepository

    @MockK
    lateinit var mfaAccountMethodsRepository: MfaAccountMethodsRepository

    lateinit var underTest: MfaMethodsEnrollmentAssociation

    @BeforeEach
    fun setUp() {
        underTest = MfaMethodsEnrollmentAssociation(ticketRepository, mfaAccountMethodsRepository)
    }

    @Test
    fun `when an email association can be associated`() {
        every { ticketRepository.loadFor(verificationTicket) } returns Optional.of(
            ticket
        )
        every { mfaAccountMethodsRepository.findAll(email) } returns emptyList()
        every { mfaAccountMethodsRepository.save(email, MfaMethod.EMAIL_MFA_METHOD) } returns mfaAccountMethod
        every { ticketRepository.delete(ticket.verificationTicket) } just runs


        underTest.associate(RAW_TICKET, MfaMethod.EMAIL_MFA_METHOD)

        verify { ticketRepository.loadFor(verificationTicket) }
        verify { mfaAccountMethodsRepository.findAll(email) }
        verify { mfaAccountMethodsRepository.save(email, MfaMethod.EMAIL_MFA_METHOD) }
        verify { ticketRepository.delete(ticket.verificationTicket) }
    }

    @Test
    fun `when an email is already associated`() {
        every { ticketRepository.loadFor(verificationTicket) } returns Optional.of(
            ticket
        )
        every { mfaAccountMethodsRepository.findAll(email) } returns listOf(mfaAccountMethod)
        every { ticketRepository.delete(ticket.verificationTicket) } just runs

        underTest.associate(RAW_TICKET, MfaMethod.EMAIL_MFA_METHOD)

        verify { ticketRepository.loadFor(verificationTicket) }
        verify { mfaAccountMethodsRepository.findAll(email) }
        verify(exactly = 0) { mfaAccountMethodsRepository.save(email, MfaMethod.EMAIL_MFA_METHOD) }
        verify { ticketRepository.delete(ticket.verificationTicket) }
    }
}