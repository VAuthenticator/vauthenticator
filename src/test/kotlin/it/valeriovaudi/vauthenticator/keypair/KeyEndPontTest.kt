package it.valeriovaudi.vauthenticator.keypair

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders.*

@ExtendWith(MockKExtension::class)
internal class KeyEndPontTest {

    lateinit var mokMvc: MockMvc

    @MockK
    lateinit var keyRepository: KeyRepository

    @BeforeEach
    internal fun setUp() {
        mokMvc = standaloneSetup(KeyEndPont("https://vauthenticator.com","A_MASTER_KEY",keyRepository)).build()
    }

    @Test
    internal fun `when we are able to create a new key`() {
        every { keyRepository.createKeyFrom("A_MASTER_KEY") } returns "123"

        mokMvc.perform(post("/keys"))
                .andExpect(status().isCreated)
                .andExpect(header().string("Location", "https://vauthenticator.com/keys/123"))

    }
}