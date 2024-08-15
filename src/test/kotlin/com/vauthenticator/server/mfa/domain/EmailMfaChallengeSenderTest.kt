package com.vauthenticator.server.mfa.domain

import com.vauthenticator.server.account.repository.AccountRepository
import com.vauthenticator.server.email.EMailSenderService
import com.vauthenticator.server.support.MfaFixture.account
import com.vauthenticator.server.support.MfaFixture.associatedMfaAccountMethod
import com.vauthenticator.server.support.MfaFixture.challenge
import com.vauthenticator.server.support.MfaFixture.email
import com.vauthenticator.server.support.MfaFixture.mfaDeviceId
import com.vauthenticator.server.support.MfaFixture.mfaSecret
import com.vauthenticator.server.support.MfaFixture.userName
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@ExtendWith(MockKExtension::class)
class EmailMfaChallengeSenderTest {

    @MockK
    lateinit var otp: OtpMfa

    @MockK
    lateinit var accountRepository: AccountRepository

    @MockK
    lateinit var mfaMailSender: EMailSenderService

    @MockK
    lateinit var mfaAccountMethodsRepository: MfaAccountMethodsRepository

    lateinit var uut: EmailMfaChallengeSender

    @BeforeEach
    fun setUp() {
        uut = EmailMfaChallengeSender(accountRepository, otp, mfaMailSender, mfaAccountMethodsRepository)
    }

    @Test
    fun `when a otp is sent via mail with a defined mfa device id`() {
        every { mfaAccountMethodsRepository.findBy(mfaDeviceId) } returns associatedMfaAccountMethod(userName, email)
        every { accountRepository.accountFor(userName) } returns Optional.of(account)
        every { otp.generateSecretKeyFor(account, MfaMethod.EMAIL_MFA_METHOD, email) } returns mfaSecret
        every { otp.getTOTPCode(mfaSecret) } returns challenge
        every {
            mfaMailSender.sendFor(
                account,
                mapOf("email" to email, "mfaCode" to challenge.content())
            )
        } just runs

        uut.sendMfaChallengeFor(userName, mfaDeviceId)
    }

    @Test
    fun `when a otp is sent via mail when it is the default mfa enrolled device`() {
        every { mfaAccountMethodsRepository.getDefaultDevice(userName) } returns Optional.of(mfaDeviceId)
        every { mfaAccountMethodsRepository.findBy(mfaDeviceId) } returns associatedMfaAccountMethod(userName, email)
        every { accountRepository.accountFor(userName) } returns Optional.of(account)
        every { otp.generateSecretKeyFor(account, MfaMethod.EMAIL_MFA_METHOD, email) } returns mfaSecret
        every { otp.getTOTPCode(mfaSecret) } returns challenge
        every {
            mfaMailSender.sendFor(
                account,
                mapOf("email" to email, "mfaCode" to challenge.content())
            )
        } just runs

        uut.sendMfaChallengeFor(userName)
    }
}