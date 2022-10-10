package it.valeriovaudi.vauthenticator.account.resetpassword

import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import it.valeriovaudi.vauthenticator.account.tiket.VerificationTicket
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@ExtendWith(MockKExtension::class)
internal class ResetPasswordEndPointTest {

    lateinit var mokMvc: MockMvc


    @MockK
    lateinit var sendResetPasswordMailChallenge: SendResetPasswordMailChallenge

    @MockK
    lateinit var resetPasswordChallengeSent: ResetPasswordChallengeSent

    @BeforeEach
    internal fun setUp() {
        mokMvc = MockMvcBuilders.standaloneSetup(ResetPasswordEndPoint(sendResetPasswordMailChallenge, resetPasswordChallengeSent))
                .build()
    }

    @Test
    internal fun `when a challenge is sent`() {
        every { sendResetPasswordMailChallenge.sendResetPasswordMail("email@domain.com") } just runs

        mokMvc.perform(MockMvcRequestBuilders.put("/api/mail/{mail}/rest-password-challenge", "email@domain.com"))
                .andExpect(MockMvcResultMatchers.status().isNoContent)
    }

    @Test
    internal fun `when a password is reset`() {
        val objectMapper = ObjectMapper()
        val request = ResetPasswordRequest("A_NEW_PSWD")
        val ticket = "A_TICKET"

        every { resetPasswordChallengeSent.resetPassword(VerificationTicket(ticket) , request) } just runs
        mokMvc.perform(MockMvcRequestBuilders.put("/api/reset-password/{ticket}", ticket)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request))
        )
                .andExpect(MockMvcResultMatchers.status().isNoContent)
    }


}