package com.vauthenticator.server.mfa.web

import com.vauthenticator.server.i18n.I18nMessageInjector
import com.vauthenticator.server.i18n.I18nScope
import com.vauthenticator.server.keys.Kid
import com.vauthenticator.server.mfa.domain.*
import com.vauthenticator.server.support.AccountTestFixture.anAccount
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
import java.util.*

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
    private lateinit var i18nMessageInjector: I18nMessageInjector

    @MockK
    private lateinit var otpMfaSender: OtpMfaSender

    @MockK
    private lateinit var otpMfaVerifier: OtpMfaVerifier

    @MockK
    private lateinit var mfaAccountMethodsRepository: MfaAccountMethodsRepository

    private val account = anAccount()

    @BeforeEach
    internal fun setUp() {
        mokMvc = MockMvcBuilders.standaloneSetup(
            MfaController(
                mfaAccountMethodsRepository,
                i18nMessageInjector,
                publisher,
                successHandler,
                failureHandler,
                otpMfaSender,
                otpMfaVerifier
            )
        ).build()
    }

    @Test
    internal fun `when an mfa challenge is sent`() {
        val mfaDeviceId = MfaDeviceId("A_MFA_DEVICE_ID")
        every { mfaAccountMethodsRepository.getDefaultDevice(account.email) } returns Optional.of(mfaDeviceId)
        every { mfaAccountMethodsRepository.findBy(mfaDeviceId) } returns Optional.of(
            MfaAccountMethod(account.email, mfaDeviceId, Kid("A_KID"), MfaMethod.EMAIL_MFA_METHOD, account.email, true)
        )
        every { otpMfaSender.sendMfaChallengeFor(account.email, MfaMethod.EMAIL_MFA_METHOD, account.email) } just runs

        mokMvc.perform(
            get("/mfa-challenge/send")
                .principal(principalFor(account.email))
        ).andExpect(redirectedUrl("/mfa-challenge"))
        verify { otpMfaSender.sendMfaChallengeFor(account.email, MfaMethod.EMAIL_MFA_METHOD, account.email) }
    }

    @Test
    internal fun `when an mfa challenge is rendered`() {
        every { i18nMessageInjector.setMessagedFor(I18nScope.MFA_PAGE, any()) } just runs

        mokMvc.perform(
            get("/mfa-challenge")
                .principal(principalFor(account.email))
        ).andExpect(view().name("template"))

        verify { i18nMessageInjector.setMessagedFor(I18nScope.MFA_PAGE, any()) }
    }

    @Test
    internal fun `when an mfa challenge is verified`() {
        every {
            otpMfaVerifier.verifyAssociatedMfaChallengeFor(
                account.email,
                MfaMethod.EMAIL_MFA_METHOD,
                account.email,
                MfaChallenge("AN_MFA_CHALLENGE_CODE")
            )
        } just runs

        val mfaAuthentication = principalFor(account.email)
        every { successHandler.onAuthenticationSuccess(any(), any(), mfaAuthentication) } just runs

        mokMvc.perform(
            post("/mfa-challenge")
                .requestAttr("mfa-code", "AN_MFA_CHALLENGE_CODE")
                .principal(mfaAuthentication)
        )
    }
}