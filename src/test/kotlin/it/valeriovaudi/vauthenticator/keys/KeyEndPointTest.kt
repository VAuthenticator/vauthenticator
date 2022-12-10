package it.valeriovaudi.vauthenticator.keys

import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders.*
import java.security.KeyPairGenerator


@ExtendWith(MockKExtension::class)
internal class KeyEndPointTest {

    private lateinit var mokMvc: MockMvc

    @MockK
    lateinit var keyRepository: KeyRepository

    private val mapper = ObjectMapper()
    private val payload = mapOf("masterKey" to "A_MASTER_KEY", "kid" to "A_KID")
    private val deletePayload = mapOf( "kid" to "A_KID", "key_purpose" to "SIGNATURE")

    @BeforeEach
    internal fun setUp() {
        mokMvc = standaloneSetup(KeyEndPoint("A_MASTER_KEY", keyRepository)).build()
    }

    @Test
    internal fun `when we are able to load master key, kid of all  keys`() {
        val kpg = KeyPairGenerator.getInstance("RSA")
        kpg.initialize(2048)

        every { keyRepository.signatureKeys() } returns Keys(
            listOf(
                Key(
                    DataKey.from("", ""),
                    MasterKid("A_MASTER_KEY"),
                    Kid("A_KID"),
                    true,
                    KeyType.ASYMMETRIC,
                    KeyPurpose.SIGNATURE
                )
            )
        )

        mokMvc.perform(get("/api/keys"))
            .andExpect(status().isOk)
            .andExpect(content().json(mapper.writeValueAsString(listOf(payload))))
    }

    @Test
    internal fun `when we are able to create a new key`() {
        every { keyRepository.createKeyFrom(MasterKid("A_MASTER_KEY")) } returns Kid("123")

        mokMvc.perform(post("/api/keys"))
            .andExpect(status().isCreated)

    }

    @Test
    internal fun `when we are able to delete a new key`() {
        every { keyRepository.deleteKeyFor( Kid("A_KID"), KeyPurpose.SIGNATURE) } just runs

        mokMvc.perform(
            delete("/api/keys")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(deletePayload))
        )
            .andExpect(status().isNoContent)
    }
}