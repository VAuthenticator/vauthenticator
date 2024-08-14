package com.vauthenticator.server.mfa.domain

import com.vauthenticator.server.account.repository.AccountRepository
import com.vauthenticator.server.email.EMailSenderService
import com.vauthenticator.server.keys.Kid
import com.vauthenticator.server.support.AccountTestFixture.anAccount
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
internal class OtpMfaEmailSenderTest {
    private val mfaSecret = MfaSecret("AN_MFA_SECRET")
    private val mfaDeviceId = MfaDeviceId("A_MFA_DEVICE_ID")
    private val mfaChallenge = MfaChallenge("A_MFA_CHALLENGE")
    private val account = anAccount()
    private val userName = account.email
    private val mfaChannel = account.email

    @MockK
    lateinit var otp: OtpMfa

    @MockK
    lateinit var accountRepository: AccountRepository

    @MockK
    lateinit var mfaMailSender: EMailSenderService

    @MockK
    lateinit var mfaAccountMethodsRepository: MfaAccountMethodsRepository

    lateinit var uut: OtpMfaEmailSender

    @BeforeEach
    fun setUp() {
        uut = OtpMfaEmailSender(accountRepository, otp, mfaMailSender, mfaAccountMethodsRepository)
    }

    @Test
    internal fun `when a otp is sent via mail with a defined mfa device id`() {
        every { mfaAccountMethodsRepository.findBy(mfaDeviceId) } returns Optional.of(
            MfaAccountMethod(
                userName,
                mfaDeviceId,
                Kid("A_KID"),
                MfaMethod.EMAIL_MFA_METHOD,
                mfaChannel,
                true
            )
        )
        every { accountRepository.accountFor(userName) } returns Optional.of(account)
        every { otp.generateSecretKeyFor(account, MfaMethod.EMAIL_MFA_METHOD, mfaChannel) } returns mfaSecret
        every { otp.getTOTPCode(mfaSecret) } returns mfaChallenge
        every {
            mfaMailSender.sendFor(
                account,
                mapOf("email" to userName, "mfaCode" to mfaChallenge.content())
            )
        } just runs

        uut.sendMfaChallengeFor(userName, mfaDeviceId)
    }

    @Test
    internal fun `when a otp is sent via mail when it is the default mfa enrolled device`() {
        every { mfaAccountMethodsRepository.getDefaultDevice(userName) } returns Optional.of(mfaDeviceId)
        every { mfaAccountMethodsRepository.findBy(mfaDeviceId) } returns Optional.of(
            MfaAccountMethod(
                userName,
                mfaDeviceId,
                Kid("A_KID"),
                MfaMethod.EMAIL_MFA_METHOD,
                mfaChannel,
                true
            )
        )
        every { accountRepository.accountFor(userName) } returns Optional.of(account)
        every { otp.generateSecretKeyFor(account, MfaMethod.EMAIL_MFA_METHOD, mfaChannel) } returns mfaSecret
        every { otp.getTOTPCode(mfaSecret) } returns mfaChallenge
        every {
            mfaMailSender.sendFor(
                account,
                mapOf("email" to userName, "mfaCode" to mfaChallenge.content())
            )
        } just runs

        uut.sendMfaChallengeFor(userName)
    }
}