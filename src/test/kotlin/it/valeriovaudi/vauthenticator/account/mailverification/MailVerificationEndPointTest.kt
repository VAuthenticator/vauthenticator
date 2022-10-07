package it.valeriovaudi.vauthenticator.account.mailverification

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientAppId
import it.valeriovaudi.vauthenticator.support.TestingFixture
import it.valeriovaudi.vauthenticator.support.TestingFixture.signedJWTFor
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

    @BeforeEach
    internal fun setUp() {
        mokMvc = MockMvcBuilders.standaloneSetup(MailVerificationEndPoint(sendVerifyMailChallenge))
                .build()
    }

    @Test
    internal fun `when a challenge is sent`() {
        every { sendVerifyMailChallenge.sendVerifyMail("email@domain.com", ClientAppId("A_CLIENT_APP_ID")) } just runs

        val signedJWT = signedJWTFor("A_CLIENT_APP_ID", "email@domain.com")
        val principal = JwtAuthenticationToken(Jwt(TestingFixture.simpleJwtFor("A_CLIENT_APP_ID"), Instant.now(), Instant.now().plusSeconds(100), signedJWT.header.toJSONObject(), signedJWT.payload.toJSONObject()))

        mokMvc.perform(put("/api/mail/{mail}/verify-challenge", "email@domain.com")
                .principal(principal))
                .andExpect(status().isNoContent)
    }


}