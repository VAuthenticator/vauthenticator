package com.vauthenticator.server.mfa.domain

import com.vauthenticator.server.account.repository.AccountRepository
import com.vauthenticator.server.support.AccountTestFixture.anAccount
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@ExtendWith(MockKExtension::class)
internal class AccountAwareOtpMfaVerifierTest {

    @MockK
    lateinit var accountRepository: AccountRepository

    @MockK
    lateinit var otpMfa: OtpMfa

    @Test
    internal fun `when a challenge is successfully verified`() {
        val account = anAccount()
        val challenge = MfaChallenge("AN_MFA_CHALLENGE")
        val underTest = AccountAwareOtpMfaVerifier(accountRepository, otpMfa)

        every { accountRepository.accountFor(account.email) } returns Optional.of(account)
        every { otpMfa.verify(account, challenge) } just runs

        underTest.verifyMfaChallengeFor(account.email, challenge)
    }

    @Test
    internal fun `when a challenge fails on verification`() {
        val account = anAccount()
        val challenge = MfaChallenge("AN_MFA_CHALLENGE")

        every { accountRepository.accountFor(account.email) } returns Optional.of(account)
        every { otpMfa.verify(account, challenge) } throws MfaException("")

        val underTest = AccountAwareOtpMfaVerifier(accountRepository, otpMfa)

        assertThrows(MfaException::class.java) { underTest.verifyMfaChallengeFor(account.email, challenge) }
    }
}