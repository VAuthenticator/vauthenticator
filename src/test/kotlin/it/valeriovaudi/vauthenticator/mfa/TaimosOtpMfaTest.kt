package it.valeriovaudi.vauthenticator.mfa

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import it.valeriovaudi.vauthenticator.account.AccountTestFixture.anAccount
import it.valeriovaudi.vauthenticator.keys.KeyRepository
import it.valeriovaudi.vauthenticator.keys.Kid
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class TaimosOtpMfaTest {

    val account = anAccount()
    val email = account.email

    @MockK
    lateinit var mfaAccountMethodsRepository: MfaAccountMethodsRepository

    @MockK
    lateinit var keyRepository: KeyRepository

    @Test
    @Disabled
    fun `when generate a secret from account secret key`() {
        val underTest = TaimosOtpMfa(keyRepository, mfaAccountMethodsRepository, OtpConfigurationProperties(6, 60))

        every { mfaAccountMethodsRepository.findAll(email) } returns mapOf(
            MfaMethod.EMAIL_MFA_METHOD to MfaAccountMethod(email, Kid("A_KID"), MfaMethod.EMAIL_MFA_METHOD)
        )

//        every { keyRepository.keyFor(Kid("A_KID")) } returns Key()

        val actual = underTest.generateSecretKeyFor(account)
        assertEquals(MfaSecret("A_SECRET"), actual)
    }
}