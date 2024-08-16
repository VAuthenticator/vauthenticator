package com.vauthenticator.server.mfa.domain

import com.vauthenticator.server.account.repository.AccountRepository
import com.vauthenticator.server.mask.SensitiveEmailMasker
import com.vauthenticator.server.mfa.domain.MfaMethod.EMAIL_MFA_METHOD
import com.vauthenticator.server.support.AccountTestFixture.anAccount
import com.vauthenticator.server.support.ClientAppFixture.aClientAppId
import com.vauthenticator.server.support.MfaFixture.email
import com.vauthenticator.server.support.MfaFixture.mfaDeviceId
import com.vauthenticator.server.support.MfaFixture.notAssociatedMfaAccountMethod
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
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

private const val emailMfaChannel = "a_new_anmail@email.com"

@ExtendWith(MockKExtension::class)
class MfaMethodsEnrollmentTest {

    @MockK
    private lateinit var sensitiveEmailMasker: SensitiveEmailMasker

    @MockK
    private lateinit var ticketCreator: TicketCreator

    @MockK
    private lateinit var accountRepository: AccountRepository

    @MockK
    private lateinit var mfaSender: MfaChallengeSender

    @MockK
    private lateinit var mfaAccountMethodsRepository: MfaAccountMethodsRepository

    private lateinit var uut: MfaMethodsEnrollment

    private val account = anAccount()
    private val userName = account.email

    private val clientAppId = aClientAppId()
    private val ticketId = TicketId("A_TICKET")
    private val emailMfaAccountMethod = notAssociatedMfaAccountMethod(userName, email)

    @BeforeEach
    fun setUp() {
        uut = MfaMethodsEnrollment(
            accountRepository,
            ticketCreator,
            mfaSender,
            mfaAccountMethodsRepository,
            sensitiveEmailMasker
        )
    }

    @Test
    fun `when the enrolment do not send the verification code together the verification ticket`() {
        every {
            mfaAccountMethodsRepository.findBy(
                userName,
                EMAIL_MFA_METHOD,
                emailMfaChannel
            )
        } returns emailMfaAccountMethod
        every { accountRepository.accountFor(userName) } returns Optional.of(account)
        every {
            ticketCreator.createTicketFor(
                account, clientAppId, ticketContext(
                    emailMfaChannel,
                    mfaDeviceId = mfaDeviceId.content
                )
            )
        } returns ticketId

        val actual = uut.enroll(userName, EMAIL_MFA_METHOD, emailMfaChannel, clientAppId, false)

        verify {
            mfaAccountMethodsRepository.findBy(
                userName,
                EMAIL_MFA_METHOD,
                emailMfaChannel
            )
        }
        verify { accountRepository.accountFor(userName) }
        verify {
            ticketCreator.createTicketFor(
                account, clientAppId, ticketContext(
                    emailMfaChannel,
                    mfaDeviceId = mfaDeviceId.content
                )
            )
        }

        assertEquals(ticketId, actual)
    }

    @Test
    fun `when mfa device was not enrolled before `() {
        every {
            mfaAccountMethodsRepository.findBy(
                userName,
                EMAIL_MFA_METHOD,
                emailMfaChannel
            )
        } returns Optional.empty()
        every {
            mfaAccountMethodsRepository.save(
                userName,
                EMAIL_MFA_METHOD,
                emailMfaChannel,
                false
            )
        } returns emailMfaAccountMethod.get()

        every { accountRepository.accountFor(userName) } returns Optional.of(account)
        every {
            ticketCreator.createTicketFor(
                account, clientAppId, ticketContext(
                    emailMfaChannel,
                    mfaDeviceId = mfaDeviceId.content
                )
            )
        } returns ticketId

        val actual = uut.enroll(userName, EMAIL_MFA_METHOD, emailMfaChannel, clientAppId, false)

        verify {
            mfaAccountMethodsRepository.findBy(
                userName,
                EMAIL_MFA_METHOD,
                emailMfaChannel
            )
        }
        verify {
            mfaAccountMethodsRepository.save(
                userName,
                EMAIL_MFA_METHOD,
                emailMfaChannel,
                false
            )
        }
        verify { accountRepository.accountFor(userName) }
        verify {
            ticketCreator.createTicketFor(
                account, clientAppId, ticketContext(
                    emailMfaChannel,
                    mfaDeviceId = mfaDeviceId.content
                )
            )
        }

        assertEquals(ticketId, actual)
    }

    @Test
    fun `when the enrolment send the verification code together the verification ticket`() {
        every {
            mfaAccountMethodsRepository.findBy(
                userName,
                EMAIL_MFA_METHOD,
                emailMfaChannel
            )
        } returns emailMfaAccountMethod
        every { accountRepository.accountFor(userName) } returns Optional.of(account)
        every {
            ticketCreator.createTicketFor(
                account, clientAppId, ticketContext(
                    emailMfaChannel,
                    mfaDeviceId =  mfaDeviceId.content
                )
            )
        } returns ticketId
        every {
            mfaSender.sendMfaChallengeFor(
                userName,
                mfaDeviceId
            )
        } just runs

        val actual = uut.enroll(userName, EMAIL_MFA_METHOD, emailMfaChannel, clientAppId, true)

        verify {
            mfaAccountMethodsRepository.findBy(
                userName,
                EMAIL_MFA_METHOD,
                emailMfaChannel
            )
        }
        verify { accountRepository.accountFor(userName) }
        verify {
            ticketCreator.createTicketFor(
                account, clientAppId, ticketContext(
                    emailMfaChannel,
                    mfaDeviceId = mfaDeviceId.content
                )
            )
        }
        verify {
            mfaSender.sendMfaChallengeFor(
                userName,
                mfaDeviceId
            )
        }

        assertEquals(ticketId, actual)
    }
}