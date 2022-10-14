package it.valeriovaudi.vauthenticator.account.mailverification

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@ExtendWith(MockKExtension::class)
internal class MailVerificationControllerTest {

    lateinit var mokMvc: MockMvc

    @MockK
    lateinit var mailVerifyMailChallengeSent: VerifyMailChallengeSent

    @BeforeEach
    internal fun setUp() {
        mokMvc = MockMvcBuilders.standaloneSetup(MailVerificationController(mailVerifyMailChallengeSent))
                .build()
    }

    @Test
    internal fun `when the challenge is verified`() {
        every { mailVerifyMailChallengeSent.verifyMail("A_TICKET") } just runs

        mokMvc.perform(MockMvcRequestBuilders.get("/mail-verify/A_TICKET"))
                .andExpect(MockMvcResultMatchers.status().isOk)
    }
}