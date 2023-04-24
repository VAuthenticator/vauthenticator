package com.vauthenticator.server.account.mailverification

import com.vauthenticator.server.oauth2.clientapp.ClientApplicationRepository
import com.vauthenticator.server.oauth2.clientapp.Scope
import com.vauthenticator.server.role.PermissionValidator
import com.vauthenticator.server.support.SecurityFixture
import com.vauthenticator.server.support.SecurityFixture.signedJWTFor
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.time.Instant

@ExtendWith(MockKExtension::class)
internal class MailVerificationEndPointTest {

    lateinit var mokMvc: MockMvc

    @MockK
    lateinit var sendVerifyMailChallenge: SendVerifyMailChallenge

    @MockK
    lateinit var cientApplicationRepository: ClientApplicationRepository

    @BeforeEach
    internal fun setUp() {
        mokMvc = MockMvcBuilders.standaloneSetup(MailVerificationEndPoint(PermissionValidator(cientApplicationRepository), sendVerifyMailChallenge))
                .build()
    }

    @Test
    internal fun `when a challenge is sent`() {
        every { sendVerifyMailChallenge.sendVerifyMail("email@domain.com") } just runs

        val signedJWT = signedJWTFor("A_CLIENT_APP_ID", "email@domain.com", listOf(Scope.MAIL_VERIFY.content))
        val principal = JwtAuthenticationToken(Jwt(SecurityFixture.simpleJwtFor("A_CLIENT_APP_ID"), Instant.now(), Instant.now().plusSeconds(100), signedJWT.header.toJSONObject(), signedJWT.payload.toJSONObject()))

        mokMvc.perform(put("/api/mail/{mail}/verify-challenge", "email@domain.com")
                .principal(principal))
                .andExpect(status().isNoContent)
    }


}