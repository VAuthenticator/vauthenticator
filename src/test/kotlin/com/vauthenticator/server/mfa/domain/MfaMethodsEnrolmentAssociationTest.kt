package com.vauthenticator.server.mfa.domain

import com.vauthenticator.server.keys.Kid
import com.vauthenticator.server.mfa.repository.MfaAccountMethodsRepository
import com.vauthenticator.server.support.AccountTestFixture.anAccount
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class MfaMethodsEnrolmentAssociationTest {
    val account = anAccount()
    val email = account.email

    private val mfaAccountMethod = MfaAccountMethod(
        email,
        Kid(""),
        MfaMethod.EMAIL_MFA_METHOD
    )

    @MockK
    lateinit var mfaAccountMethodsRepository: MfaAccountMethodsRepository

    lateinit var underTest: MfaMethodsEnrolmentAssociation

    @BeforeEach
    fun setUp() {
        underTest = MfaMethodsEnrolmentAssociation(mfaAccountMethodsRepository)
    }

    @Test
    fun `when an email association goes fine`() {
        every { mfaAccountMethodsRepository.findAll(email) } returns emptyMap()
        every { mfaAccountMethodsRepository.save(email, MfaMethod.EMAIL_MFA_METHOD) } returns mfaAccountMethod

        underTest.associate(account, MfaMethod.EMAIL_MFA_METHOD)

        verify { mfaAccountMethodsRepository.findAll(email) }
        verify { mfaAccountMethodsRepository.save(email, MfaMethod.EMAIL_MFA_METHOD) }
    }

    @Test
    fun `when an email association is already done`() {
        every { mfaAccountMethodsRepository.findAll(email) } returns mapOf(MfaMethod.EMAIL_MFA_METHOD to mfaAccountMethod)

        underTest.associate(account, MfaMethod.EMAIL_MFA_METHOD)

        verify { mfaAccountMethodsRepository.findAll(email) }
        verify(exactly = 0) { mfaAccountMethodsRepository.save(email, MfaMethod.EMAIL_MFA_METHOD) }
    }
}