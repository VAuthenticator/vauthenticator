package it.valeriovaudi.vauthenticator.mfa

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import it.valeriovaudi.vauthenticator.account.AccountTestFixture.anAccount
import it.valeriovaudi.vauthenticator.account.repository.AccountRepository
import it.valeriovaudi.vauthenticator.mail.MailSenderService
import org.junit.jupiter.api.Assertions.*
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
    lateinit var mfaMailSender: MailSenderService

    @Test
    internal fun `when a otp is sent via mail`() {
        val mfaSecret = MfaSecret("AN_MFA_SECRET")
        val mfaChallenge = MfaChallenge("A_MFA_CHALLENGE")
        val account = anAccount()
        val underTest = OtpMfaEmailSender(accountRepository, otp, mfaMailSender)

        every { accountRepository.accountFor(account.email) } returns Optional.of(account)
        every { otp.generateSecretKeyFor(account) } returns mfaSecret
        every { otp.getTOTPCode(mfaSecret) } returns mfaChallenge
        every { mfaMailSender.sendFor(account, mapOf("mfaCode" to mfaChallenge.content())) } just runs

        underTest.sendMfaChallenge(account.email)
    }
}