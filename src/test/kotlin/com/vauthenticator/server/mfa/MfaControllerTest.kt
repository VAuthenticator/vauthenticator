package com.vauthenticator.server.mfa

import com.fasterxml.jackson.databind.ObjectMapper
import com.vauthenticator.server.account.AccountTestFixture.anAccount
import com.vauthenticator.server.support.SecurityFixture.principalFor
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.context.ApplicationEventPublisher
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.view
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@ExtendWith(MockKExtension::class)
internal class MfaControllerTest {
    lateinit var mokMvc: MockMvc

    @MockK
    private lateinit var successHandler: AuthenticationSuccessHandler

    @MockK
    private lateinit var failureHandler: AuthenticationFailureHandler

    @MockK
    private lateinit var publisher: ApplicationEventPublisher

    @MockK
    private lateinit var otpMfaSender: OtpMfaSender

    @MockK
    private lateinit var otpMfaVerifier: OtpMfaVerifier
    private val account = anAccount()

    @BeforeEach
    internal fun setUp() {
        mokMvc = MockMvcBuilders.standaloneSetup(
            MfaController(
                publisher,
                ObjectMapper(),
                successHandler, failureHandler,
                otpMfaSender, otpMfaVerifier
            )
        ).build()
    }

    @Test
    internal fun `when an mfa challenge is sent`() {
        every { otpMfaSender.sendMfaChallenge(account.email) } just runs

        mokMvc.perform(
            get("/mfa-challenge/send")
                .principal(principalFor(account.email))
        ).andExpect(redirectedUrl("/mfa-challenge"))
        verify { otpMfaSender.sendMfaChallenge(account.email) }
    }

    @Test
    internal fun `when an mfa challenge is rendered`() {
        mokMvc.perform(
            get("/mfa-challenge")
                .principal(principalFor(account.email))
        ).andExpect(view().name("template"))
    }

    @Test
    internal fun `when an mfa challenge is verified`() {
        every { otpMfaVerifier.verifyMfaChallengeFor(account.email, MfaChallenge("AN_MFA_CHALLENGE_CODE")) } just runs

        val mfaAuthentication = principalFor(account.email)
        every { successHandler.onAuthenticationSuccess(any(), any(), mfaAuthentication) } just runs

        mokMvc.perform(
            post("/mfa-challenge")
                .requestAttr("mfa-code", "AN_MFA_CHALLENGE_CODE")
                .principal(mfaAuthentication)
        )
    }
}