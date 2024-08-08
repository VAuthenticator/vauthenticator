package com.vauthenticator.server.mfa.api

import com.vauthenticator.server.keys.Kid
import com.vauthenticator.server.mfa.domain.*
import com.vauthenticator.server.support.AccountTestFixture
import com.vauthenticator.server.support.SecurityFixture
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.util.*

@ExtendWith(MockKExtension::class)
internal class MfaChallengeEndPointTest {
    lateinit var mokMvc: MockMvc

    @MockK
    private lateinit var otpMfaSender: OtpMfaSender
    @MockK
    private lateinit var mfaAccountMethodsRepository: MfaAccountMethodsRepository

    private val account = AccountTestFixture.anAccount()

    @BeforeEach
    internal fun setUp() {
        mokMvc = MockMvcBuilders.standaloneSetup(
            MfaChallengeEndPoint(mfaAccountMethodsRepository, otpMfaSender)
        ).build()
    }


    @Test
    internal fun `when an mfa challenge is sent to the default mfa device`() {
        val mfaDeviceId = MfaDeviceId("A_MFA_DEVICE_ID")
        every { mfaAccountMethodsRepository.getDefaultDevice(account.email) } returns Optional.of(mfaDeviceId)
        every { mfaAccountMethodsRepository.findBy(mfaDeviceId) } returns Optional.of(
            MfaAccountMethod(account.email, mfaDeviceId, Kid("A_KID"), MfaMethod.EMAIL_MFA_METHOD, account.email, true)
        )
        every { otpMfaSender.sendMfaChallenge(account.email, MfaMethod.EMAIL_MFA_METHOD, account.email) } just runs

        mokMvc.perform(
            put("/api/mfa/challenge")
                .principal(SecurityFixture.principalFor(account.email))
        ).andExpect(status().isOk)
    }

    @Test
    internal fun `when an mfa challenge is sent to a specific mfa device`() {
        val mfaChannel = "another_email@email.com"
        val mfaDeviceId = MfaDeviceId("A_WELL_DEFINED_MFA_DEVICE_ID")

        every { mfaAccountMethodsRepository.findBy(mfaDeviceId) } returns Optional.of(
            MfaAccountMethod(account.email, mfaDeviceId, Kid("A_KID"), MfaMethod.EMAIL_MFA_METHOD, mfaChannel, true)
        )
        every { otpMfaSender.sendMfaChallenge(account.email, MfaMethod.EMAIL_MFA_METHOD, mfaChannel) } just runs

        mokMvc.perform(
            put("/api/mfa/challenge")
                .param("mfa-device-id", "A_WELL_DEFINED_MFA_DEVICE_ID")
                .principal(SecurityFixture.principalFor(account.email))
        ).andExpect(status().isOk)
    }
}