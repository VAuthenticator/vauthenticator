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
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@ExtendWith(MockKExtension::class)
internal class OtpMfaEmailSenderTest {

    @MockK
    lateinit var otp: OtpMfa

    @MockK
    lateinit var accountRepository: AccountRepository

    @MockK
    lateinit var mfaMailSender: EMailSenderService

    @MockK
    lateinit var mfaAccountMethodsRepository : MfaAccountMethodsRepository

    @Test
    internal fun `when a otp is sent via mail`() {
        val mfaSecret = MfaSecret("AN_MFA_SECRET")
        val mfaChallenge = MfaChallenge("A_MFA_CHALLENGE")
        val account = anAccount()
        val underTest = OtpMfaEmailSender(accountRepository, otp, mfaMailSender, mfaAccountMethodsRepository)

        every { accountRepository.accountFor(account.email) } returns Optional.of(account)
        every { otp.generateSecretKeyFor(account, MfaMethod.EMAIL_MFA_METHOD, account.email) } returns mfaSecret
        every { otp.getTOTPCode(mfaSecret) } returns mfaChallenge
        every {
            mfaMailSender.sendFor(
                account,
                mapOf("email" to account.email, "mfaCode" to mfaChallenge.content())
            )
        } just runs

        underTest.sendMfaChallengeFor(account.email, MfaMethod.EMAIL_MFA_METHOD, account.email)
    }

    @Test
    internal fun `when a otp is sent via mail with a  defined mfa device id`() {
        val mfaSecret = MfaSecret("AN_MFA_SECRET")
        val mfaDeviceId = MfaDeviceId("A_MFA_DEVICE_ID")
        val mfaChallenge = MfaChallenge("A_MFA_CHALLENGE")
        val account = anAccount()
        val underTest = OtpMfaEmailSender(accountRepository, otp, mfaMailSender, mfaAccountMethodsRepository)

        val userName = account.email
        val mfaChannel = account.email

        every { accountRepository.accountFor(userName) } returns Optional.of(account)
        every { mfaAccountMethodsRepository.findBy(mfaDeviceId) } returns Optional.of(MfaAccountMethod(userName, mfaDeviceId, Kid("A_KID"), MfaMethod.EMAIL_MFA_METHOD, mfaChannel, true))
        every { otp.generateSecretKeyFor(account, MfaMethod.EMAIL_MFA_METHOD, mfaChannel) } returns mfaSecret
        every { otp.getTOTPCode(mfaSecret) } returns mfaChallenge
        every {
            mfaMailSender.sendFor(
                account,
                mapOf("email" to userName, "mfaCode" to mfaChallenge.content())
            )
        } just runs

        underTest.sendMfaChallengeFor(userName, mfaDeviceId)
    }
}