package com.vauthenticator.server.mfa.domain

import com.vauthenticator.server.account.repository.AccountRepository
import com.vauthenticator.server.keys.Kid
import com.vauthenticator.server.support.AccountTestFixture.anAccount
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@ExtendWith(MockKExtension::class)
class AccountAwareOtpMfaVerifierAssociationTest {

    private val account = anAccount()
    private val userName = account.email
    private val email = "a_new_email@email.com"
    private val challenge = MfaChallenge("AN_MFA_CHALLENGE")


    @MockK
    lateinit var accountRepository: AccountRepository

    @MockK
    lateinit var mfaAccountMethodsRepository: MfaAccountMethodsRepository

    @MockK
    lateinit var otpMfa: OtpMfa

    lateinit var underTest: OtpMfaVerifier
    private val mfaDeviceId = MfaDeviceId("A_MFA_DEVICE_ID")
    private val keyId = Kid("A_KID")

    @BeforeEach
    fun setUp() {
        underTest = AccountAwareOtpMfaVerifier(accountRepository, otpMfa, mfaAccountMethodsRepository)
    }

    @Test
    fun `when not associated mfa verification succeed on association verification`() {
        every { accountRepository.accountFor(account.email) } returns Optional.of(account)
        every { otpMfa.verify(account, MfaMethod.EMAIL_MFA_METHOD, email, challenge) } throws MfaException("")
        every { mfaAccountMethodsRepository.findBy(userName, MfaMethod.EMAIL_MFA_METHOD, email) } returns
                Optional.of(
                    MfaAccountMethod(
                        userName,
                        mfaDeviceId,
                        keyId,
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

        verify { mfaAccountMethodsRepository.findBy(userName, MfaMethod.EMAIL_MFA_METHOD, email) }
    }

    @Test
    fun `when associated mfa verification succeed on association verification`() {
        every { accountRepository.accountFor(account.email) } returns Optional.of(account)
        every { otpMfa.verify(account, MfaMethod.EMAIL_MFA_METHOD, email, challenge) } throws MfaException("")
        every { mfaAccountMethodsRepository.findBy(userName, MfaMethod.EMAIL_MFA_METHOD, email) } returns
                Optional.of(
                    MfaAccountMethod(
                        userName,
                        mfaDeviceId,
                        keyId,
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

        verify { mfaAccountMethodsRepository.findBy(userName, MfaMethod.EMAIL_MFA_METHOD, email) }
    }
}