package com.vauthenticator.server.mfa.domain

import com.vauthenticator.server.extentions.decoder
import com.vauthenticator.server.keys.*
import com.vauthenticator.server.mfa.OtpConfigurationProperties
import com.vauthenticator.server.mfa.repository.MfaAccountMethodsRepository
import com.vauthenticator.server.support.AccountTestFixture.anAccount
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.apache.commons.codec.binary.Hex
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.Optional.of

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
            KeyPurpose.MFA,
            0L
        )
        every { mfaAccountMethodsRepository.findOne(email, MfaMethod.EMAIL_MFA_METHOD) } returns
                of(MfaAccountMethod(email, Kid("A_KID"), MfaMethod.EMAIL_MFA_METHOD))

        every { keyRepository.keyFor(Kid("A_KID"), KeyPurpose.MFA) } returns key
        every { keyDecrypter.decryptKey("QV9FTkNSWVBURURfS0VZ") } returns "QV9ERUNSWVBURURfU1lNTUVUUklDX0tFWQ=="
        val actual = underTest.generateSecretKeyFor(account)
        val expectedSecret = Hex.encodeHexString(decoder.decode("QV9ERUNSWVBURURfU1lNTUVUUklDX0tFWQ=="))
        assertEquals(MfaSecret(expectedSecret), actual)
    }
}
