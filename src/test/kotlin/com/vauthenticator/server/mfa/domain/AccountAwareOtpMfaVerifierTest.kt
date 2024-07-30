package com.vauthenticator.server.mfa.domain

import com.vauthenticator.server.account.repository.AccountRepository
import com.vauthenticator.server.keys.Kid
import com.vauthenticator.server.support.AccountTestFixture.anAccount
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@ExtendWith(MockKExtension::class)
internal class AccountAwareOtpMfaVerifierTest {

    private val account = anAccount()
    private val userName = account.email
    private val email =  "a_new_email@email.com"
    private val challenge = MfaChallenge("AN_MFA_CHALLENGE")


    @MockK
    lateinit var accountRepository: AccountRepository

    @MockK
    lateinit var mfaAccountMethodsRepository: MfaAccountMethodsRepository

    @MockK
    lateinit var otpMfa: OtpMfa

    lateinit var underTest: OtpMfaVerifier

    @BeforeEach
    fun setUp() {
        underTest = AccountAwareOtpMfaVerifier(accountRepository, otpMfa, mfaAccountMethodsRepository)
    }

    @Test
    internal fun `when associated mfa challenge is successfully verified`() {
        val challenge = MfaChallenge("AN_MFA_CHALLENGE")

        every { mfaAccountMethodsRepository.findOne(userName, MfaMethod.EMAIL_MFA_METHOD, email) } returns
                Optional.of(
                    MfaAccountMethod(
                        userName,
                        Kid("A_KID"),
                        MfaMethod.EMAIL_MFA_METHOD,
                        email,
                        true
                    )
                )
        every { accountRepository.accountFor(userName) } returns Optional.of(account)
        every { otpMfa.verify(account, MfaMethod.EMAIL_MFA_METHOD, email, challenge) } just runs

        underTest.verifyAssociatedMfaChallengeFor(userName, MfaMethod.EMAIL_MFA_METHOD, email, challenge)

        verify { mfaAccountMethodsRepository.findOne(userName, MfaMethod.EMAIL_MFA_METHOD, email) }
    }

    @Test
    internal fun `when not associated mfa challenge fails on verification`() {
        every { mfaAccountMethodsRepository.findOne(userName, MfaMethod.EMAIL_MFA_METHOD, email) } returns
                Optional.of(
                    MfaAccountMethod(
                        userName,
                        Kid("A_KID"),
                        MfaMethod.EMAIL_MFA_METHOD,
                        email,
                        false
                    )
                )
        every { accountRepository.accountFor(userName) } returns Optional.of(account)
        every { otpMfa.verify(account, MfaMethod.EMAIL_MFA_METHOD, email, challenge) } just runs

        assertThrows(UnAssociatedMfaVerificationException::class.java) {
            underTest.verifyAssociatedMfaChallengeFor(
                userName,
                MfaMethod.EMAIL_MFA_METHOD,
                email,
                challenge
            )
        }

        verify { mfaAccountMethodsRepository.findOne(userName, MfaMethod.EMAIL_MFA_METHOD, email) }
    }

    @Test
    internal fun `when associated mfa challenge fails on verification`() {
        every { accountRepository.accountFor(account.email) } returns Optional.of(account)
        every { otpMfa.verify(account, MfaMethod.EMAIL_MFA_METHOD, email, challenge) } throws MfaException("")
        every { mfaAccountMethodsRepository.findOne(userName, MfaMethod.EMAIL_MFA_METHOD, email) } returns
                Optional.of(
                    MfaAccountMethod(
                        userName,
                        Kid("A_KID"),
                        MfaMethod.EMAIL_MFA_METHOD,
                        email,
                        true
                    )
                )
        assertThrows(MfaException::class.java) {
            underTest.verifyAssociatedMfaChallengeFor(
                userName,
                MfaMethod.EMAIL_MFA_METHOD,
                email,
                challenge
            )
        }

        verify { mfaAccountMethodsRepository.findOne(userName, MfaMethod.EMAIL_MFA_METHOD, email) }
    }



    @Test
    internal fun `when not associated mfa verification succeed on association verification`() {
        every { accountRepository.accountFor(account.email) } returns Optional.of(account)
        every { otpMfa.verify(account, MfaMethod.EMAIL_MFA_METHOD, email, challenge) } throws MfaException("")
        every { mfaAccountMethodsRepository.findOne(userName, MfaMethod.EMAIL_MFA_METHOD, email) } returns
                Optional.of(
                    MfaAccountMethod(
                        userName,
                        Kid("A_KID"),
                        MfaMethod.EMAIL_MFA_METHOD,
                        email,
                        false
                    )
                )
        assertThrows(MfaException::class.java) {
            underTest.verifyMfaChallengeToBeAssociatedFor(
                account.email,
                MfaMethod.EMAIL_MFA_METHOD,
                email,
                challenge
            )
        }

        verify { mfaAccountMethodsRepository.findOne(userName, MfaMethod.EMAIL_MFA_METHOD, email) }
    }

    @Test
    internal fun `when associated mfa verification succeed on association verification`() {
        every { accountRepository.accountFor(account.email) } returns Optional.of(account)
        every { otpMfa.verify(account, MfaMethod.EMAIL_MFA_METHOD, email, challenge) } throws MfaException("")
        every { mfaAccountMethodsRepository.findOne(userName, MfaMethod.EMAIL_MFA_METHOD, email) } returns
                Optional.of(
                    MfaAccountMethod(
                        userName,
                        Kid("A_KID"),
                        MfaMethod.EMAIL_MFA_METHOD,
                        email,
                        true
                    )
                )
        assertThrows(AssociatedMfaVerificationException::class.java) {
            underTest.verifyMfaChallengeToBeAssociatedFor(
                account.email,
                MfaMethod.EMAIL_MFA_METHOD,
                email,
                challenge
            )
        }

        verify { mfaAccountMethodsRepository.findOne(userName, MfaMethod.EMAIL_MFA_METHOD, email) }
    }
}