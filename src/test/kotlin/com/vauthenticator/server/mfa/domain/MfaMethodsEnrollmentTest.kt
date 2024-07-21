package com.vauthenticator.server.mfa.domain

import com.vauthenticator.server.clientapp.ClientAppFixture.aClientAppId
import com.vauthenticator.server.support.AccountTestFixture.anAccount
import com.vauthenticator.server.ticket.VerificationTicket
import com.vauthenticator.server.ticket.VerificationTicketFactory
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
    private lateinit var verificationTicketFactory: VerificationTicketFactory

    @MockK
    private lateinit var mfaSender: OtpMfaSender


    @Test
    fun `when the enrolment do not send the verification code together the verification ticket`() {

        val uut = MfaMethodsEnrollment(
            verificationTicketFactory,
            mfaSender,
        )


        val account = anAccount()
        val clientAppId = aClientAppId()


        val verificationTicket = VerificationTicket("A_TICKET")

        every { verificationTicketFactory.createTicketFor(account, clientAppId) } returns verificationTicket
        val actual = uut.enroll(account, MfaMethod.EMAIL_MFA_METHOD, clientAppId, false)

        verify { verificationTicketFactory.createTicketFor(account, clientAppId) }

        assertEquals(verificationTicket, actual)
    }

    @Test
    fun `when the enrolment send the verification code together the verification ticket`() {

        val uut = MfaMethodsEnrollment(
            verificationTicketFactory,
            mfaSender,
        )


        val account = anAccount()
        val clientAppId = aClientAppId()


        val verificationTicket = VerificationTicket("A_TICKET")

        every { verificationTicketFactory.createTicketFor(account, clientAppId) } returns verificationTicket
        every { mfaSender.sendMfaChallenge(account.email) } just runs

        val actual = uut.enroll(account, MfaMethod.EMAIL_MFA_METHOD, clientAppId, true)

        verify { verificationTicketFactory.createTicketFor(account, clientAppId) }
        verify { mfaSender.sendMfaChallenge(account.email) }

        assertEquals(verificationTicket, actual)
    }
}