package com.vauthenticator.server.clientapp

import com.fasterxml.jackson.databind.ObjectMapper
import com.vauthenticator.server.clientapp.ClientAppFixture.aClientApp
import com.vauthenticator.server.clientapp.ClientAppFixture.aClientAppId
import com.vauthenticator.server.oauth2.clientapp.*
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders.*
import java.util.*

@ExtendWith(MockKExtension::class)
class ClientApplicationEndPointTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockK
    lateinit var clientApplicationRepository: ClientApplicationRepository

    @MockK
    lateinit var storeClientApplication: StoreClientApplication

    @MockK
    lateinit var readClientApplication: ReadClientApplication

    val objectMapper = ObjectMapper()

    @BeforeEach
    internal fun setUp() {
        mockMvc = standaloneSetup(ClientApplicationEndPoint(clientApplicationRepository, storeClientApplication, readClientApplication))
                .build()
    }


    @Test
    fun `store a new client app`() {
        val clientAppId = aClientAppId()
        val clientApplication = aClientApp(clientAppId)
        val representation = ClientAppRepresentation.fromDomainToRepresentation(clientApplication, storePassword = true)

        every { storeClientApplication.store(clientApplication, true) } just runs

        mockMvc.perform(
                put("/api/client-applications/${clientAppId.content}")
                        .content(objectMapper.writeValueAsString(representation))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNoContent)

    }

    @Test
    fun `reset password for a client app`() {
        val clientAppId = aClientAppId()
        every { storeClientApplication.resetPassword(clientAppId, Secret("secret")) } just runs

        mockMvc.perform(
                patch("/api/client-applications/${clientAppId.content}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mapOf("secret" to "secret")))
        ).andExpect(status().isNoContent)

    }

    @Test
    fun `reset password for a not existing client app`() {
        every { storeClientApplication.resetPassword(ClientAppId("clientApp"), Secret("secret")) } throws ClientApplicationNotFound("the client application clientApp was not found")

        mockMvc.perform(
                patch("/api/client-applications/clientApp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mapOf("secret" to "secret")))
        )
                .andExpect(status().isNotFound)

    }

    @Test
    fun `view all client app`() {
        val clientApplication = aClientApp(ClientAppId("clientApp"))
        val body = listOf(
                ClientAppInListRepresentation.fromDomainToRepresentation(clientApplication),
                ClientAppInListRepresentation.fromDomainToRepresentation(clientApplication),
                ClientAppInListRepresentation.fromDomainToRepresentation(clientApplication)
        )

        every { readClientApplication.findAll() } returns listOf(clientApplication, clientApplication, clientApplication)

        mockMvc.perform(get("/api/client-applications"))
                .andExpect(status().isOk)
                .andExpect(content().json(objectMapper.writeValueAsString(body)))

    }

    @Test
    fun `view a specific client app`() {
        val clientApplication = aClientApp(ClientAppId("clientApp"))
        val body = ClientAppRepresentation.fromDomainToRepresentation(clientApplication)

        every { readClientApplication.findOne(ClientAppId("clientApp")) } returns Optional.of(aClientApp(ClientAppId("clientApp")))

        mockMvc.perform(get("/api/client-applications/clientApp"))
                .andExpect(status().isOk)
                .andExpect(content().json(objectMapper.writeValueAsString(body)))

    }

    @Test
    fun `delete a specific client app`() {
        every { clientApplicationRepository.delete(ClientAppId("clientApp")) } just runs

        mockMvc.perform(delete("/api/client-applications/clientApp"))
                .andExpect(status().isNoContent)

    }
}