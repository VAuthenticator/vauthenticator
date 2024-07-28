package com.vauthenticator.server.mfa.domain

import com.vauthenticator.server.account.repository.AccountRepository
import com.vauthenticator.server.email.EMailSenderService
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

    @Test
    internal fun `when a otp is sent via mail`() {
        val mfaSecret = MfaSecret("AN_MFA_SECRET")
        val mfaChallenge = MfaChallenge("A_MFA_CHALLENGE")
        val account = anAccount()
        val underTest = OtpMfaEmailSender(accountRepository, otp, mfaMailSender)

        every { accountRepository.accountFor(account.email) } returns Optional.of(account)
        every { otp.generateSecretKeyFor(account, MfaMethod.EMAIL_MFA_METHOD, account.email) } returns mfaSecret
        every { otp.getTOTPCode(mfaSecret) } returns mfaChallenge
        every {
            mfaMailSender.sendFor(
                account,
                mapOf("email" to account.email, "mfaCode" to mfaChallenge.content())
            )
        } just runs

        underTest.sendMfaChallenge(account.email, MfaMethod.EMAIL_MFA_METHOD, account.email)
    }
}