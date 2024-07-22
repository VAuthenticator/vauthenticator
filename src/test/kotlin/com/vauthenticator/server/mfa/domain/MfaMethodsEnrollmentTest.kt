package com.vauthenticator.server.mfa.domain

import com.vauthenticator.server.clientapp.ClientAppFixture.aClientAppId
import com.vauthenticator.server.support.AccountTestFixture.anAccount
import com.vauthenticator.server.support.TicketFixture.ticketContext
import com.vauthenticator.server.ticket.TicketCreator
import com.vauthenticator.server.ticket.TicketId
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class MfaMethodsEnrollmentTest {

    @MockK
    private lateinit var ticketCreator: TicketCreator

    @MockK
    private lateinit var mfaSender: OtpMfaSender


    @Test
    fun `when the enrolment do not send the verification code together the verification ticket`() {

        val uut = MfaMethodsEnrollment(
            ticketCreator,
            mfaSender,
        )


        val account = anAccount()
        val clientAppId = aClientAppId()


        val ticketId = TicketId("A_TICKET")

        every { ticketCreator.createTicketFor(account, clientAppId, ticketContext(account.email)) } returns ticketId
        val actual = uut.enroll(account, MfaMethod.EMAIL_MFA_METHOD, account.email, clientAppId, false)
        verify {
            ticketCreator.createTicketFor(
                account, clientAppId, ticketContext(account.email)
            )
        }

        assertEquals(ticketId, actual)
    }

    @Test
    fun `when the enrolment send the verification code together the verification ticket`() {

        val uut = MfaMethodsEnrollment(
            ticketCreator,
            mfaSender,
        )


        val account = anAccount()
        val clientAppId = aClientAppId()


        val ticketId = TicketId("A_TICKET")

        every { ticketCreator.createTicketFor(account, clientAppId, ticketContext(account.email)) } returns ticketId
        every { mfaSender.sendMfaChallenge(account.email, account.email) } just runs

        val actual = uut.enroll(account, MfaMethod.EMAIL_MFA_METHOD, account.email, clientAppId, true)

        verify { ticketCreator.createTicketFor(account, clientAppId, ticketContext(account.email)) }
        verify { mfaSender.sendMfaChallenge(account.email, account.email) }

        assertEquals(ticketId, actual)
    }
}