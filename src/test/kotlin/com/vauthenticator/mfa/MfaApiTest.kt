package com.vauthenticator.mfa

import com.vauthenticator.support.SecurityFixture
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@ExtendWith(MockKExtension::class)
internal class MfaApiTest {
    lateinit var mokMvc: MockMvc

    @MockK
    private lateinit var otpMfaSender: OtpMfaSender

    private val account = com.vauthenticator.account.AccountTestFixture.anAccount()

    @BeforeEach
    internal fun setUp() {
        mokMvc = MockMvcBuilders.standaloneSetup(
            MfaApi(otpMfaSender)
        ).build()
    }


    @Test
    internal fun `when an mfa challenge is sent`() {
        every { otpMfaSender.sendMfaChallenge(account.email) } just runs

        mokMvc.perform(
            MockMvcRequestBuilders.put("/mfa-challenge/send")
                .principal(SecurityFixture.principalFor(account.email))
        ).andExpect(status().isOk)

    }
}