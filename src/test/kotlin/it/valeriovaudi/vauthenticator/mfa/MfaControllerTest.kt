package it.valeriovaudi.vauthenticator.mfa

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import it.valeriovaudi.vauthenticator.account.AccountTestFixture.anAccount
import it.valeriovaudi.vauthenticator.support.TestingFixture.mfaPrincipalFor
import it.valeriovaudi.vauthenticator.support.TestingFixture.principalFor
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@ExtendWith(MockKExtension::class)
internal class MfaControllerTest {
    lateinit var mokMvc: MockMvc

    @MockK
    private lateinit var successHandler: AuthenticationSuccessHandler

    @MockK
    private lateinit var otpMfaSender: OtpMfaSender

    @MockK
    private lateinit var otpMfaVerifier: OtpMfaVerifier
    private val account = anAccount()

    @BeforeEach
    internal fun setUp() {
        mokMvc = MockMvcBuilders.standaloneSetup(
            MfaController(successHandler, otpMfaSender, otpMfaVerifier)
        )
            .build()
    }

    @Test
    internal fun `when an mfa challenge is sent`() {
        every { otpMfaSender.sendMfaChallenge(account.email) } just runs

        mokMvc.perform(
            get("/mfa-challenge")
                .principal(principalFor(account.email))
        ).andExpect(view().name("mfa/index"))
    }

    @Test
    internal fun `when an mfa challenge is verified`() {
        every { otpMfaVerifier.verifyMfaChallengeFor(account.email, MfaChallenge("AN_MFA_CHALLENGE_CODE")) } just runs

        val mfaAuthentication = mfaPrincipalFor(account.email)
        val authentication = mfaAuthentication.delegate
        every { successHandler.onAuthenticationSuccess(any(), any(), authentication) } just runs

        mokMvc.perform(
            post("/mfa-challenge")
                .requestAttr("mfa-code", "AN_MFA_CHALLENGE_CODE")
                .principal(mfaAuthentication)
        )
    }
}