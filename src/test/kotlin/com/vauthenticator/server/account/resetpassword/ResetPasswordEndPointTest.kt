package com.vauthenticator.server.account.resetpassword

import com.fasterxml.jackson.databind.ObjectMapper
import com.vauthenticator.server.account.tiket.VerificationTicket
import com.vauthenticator.server.oauth2.clientapp.ClientAppId
import com.vauthenticator.server.support.SecurityFixture.principalFor
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@ExtendWith(MockKExtension::class)
internal class ResetPasswordEndPointTest {

    lateinit var mokMvc: MockMvc

    val clientAppId = ClientAppId("A_CLIENT_APP_ID")


    @MockK
    lateinit var sendResetPasswordMailChallenge: SendResetPasswordMailChallenge

    @MockK
    lateinit var resetAccountPassword: ResetAccountPassword

    @BeforeEach
    internal fun setUp() {
        mokMvc = MockMvcBuilders.standaloneSetup(ResetPasswordEndPoint(sendResetPasswordMailChallenge, resetAccountPassword))
                .build()
    }

    @Test
    internal fun `when a challenge is sent`() {
        every { sendResetPasswordMailChallenge.anonymousSendResetPasswordMail("email@domain.com", clientAppId) } just runs

        mokMvc.perform(put("/api/mail/{mail}/reset-password-challenge", "email@domain.com")
                .principal(principalFor("A_CLIENT_APP_ID", "email@domain.com", listOf("VAUTHENTICATOR_ADMIN"))))
                .andExpect(status().isNoContent)
    }
    @Test
    internal fun `when a password is reset as anonymous but starting from ui`() {
        every { sendResetPasswordMailChallenge.anonymousSendResetPasswordMail("email@domain.com", clientAppId) } just runs

        mokMvc.perform(put("/api/mail/{mail}/reset-password-challenge", "email@domain.com")
                .sessionAttr("clientId", "A_CLIENT_APP_ID")
        ).andExpect(status().isNoContent)
    }

    @Test
    internal fun `when a password is reset`() {
        val objectMapper = ObjectMapper()
        val request = ResetPasswordRequest("A_NEW_PSWD")
        val ticket = "A_TICKET"

        every { resetAccountPassword.resetPasswordFromMailChallenge(VerificationTicket(ticket) , request) } just runs
        mokMvc.perform(put("/api/reset-password/{ticket}", ticket)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request))
        )
                .andExpect(status().isNoContent)
    }

}