package com.vauthenticator.mfa

import com.vauthenticator.account.AccountTestFixture.anAccount
import com.vauthenticator.extentions.decoder
import com.vauthenticator.keys.*
import com.vauthenticator.mfa.*
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.apache.commons.codec.binary.Hex
import org.junit.jupiter.api.Assertions.*
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

    @MockK
    lateinit var keyDecrypter: KeyDecrypter

    @Test
    fun `when generate a secret from account secret key`() {
        val underTest =
            TaimosOtpMfa(keyDecrypter, keyRepository, mfaAccountMethodsRepository, OtpConfigurationProperties(6, 60))

        val key = Key(
            DataKey.from("QV9FTkNSWVBURURfS0VZ", ""),
            MasterKid(""),
            Kid("A_KID"),
            true,
            KeyType.SYMMETRIC,
            KeyPurpose.MFA
        )
        every { mfaAccountMethodsRepository.findAll(email) } returns mapOf(
            MfaMethod.EMAIL_MFA_METHOD to MfaAccountMethod(email, Kid("A_KID"), MfaMethod.EMAIL_MFA_METHOD)
        )
        every { keyRepository.keyFor(Kid("A_KID"), KeyPurpose.MFA) } returns key
        every { keyDecrypter.decryptKey("QV9FTkNSWVBURURfS0VZ") } returns "QV9ERUNSWVBURURfU1lNTUVUUklDX0tFWQ=="
        val actual = underTest.generateSecretKeyFor(account)
        val expectedSecret = Hex.encodeHexString(decoder.decode("QV9ERUNSWVBURURfU1lNTUVUUklDX0tFWQ=="))
        assertEquals(MfaSecret(expectedSecret), actual)
    }
}
