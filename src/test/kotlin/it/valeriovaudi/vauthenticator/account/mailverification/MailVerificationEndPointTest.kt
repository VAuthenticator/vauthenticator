package it.valeriovaudi.vauthenticator.account.mailverification

import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@ExtendWith(MockKExtension::class)
internal class MailVerificationEndPointTest {

    lateinit var mokMvc: MockMvc


    @MockK
    lateinit var mailVerificationUseCase: MailVerificationUseCase

    @BeforeEach
    internal fun setUp() {
        mokMvc = MockMvcBuilders.standaloneSetup(MailVerificationEndPoint(mailVerificationUseCase))
                .build()
    }

    @Test
    internal fun `happy path`() {
        mokMvc.perform(put("/api/mail/{mail}/verify-challenge", "email@domain.com"))
                .andExpect(status().isNoContent)
    }
}