package it.valeriovaudi.vauthenticator.oauth2.clientapp

import com.fasterxml.jackson.databind.ObjectMapper
import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientAppFixture.aClientApp
import it.valeriovaudi.vauthenticator.security.userdetails.AccountUserDetailsService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.http.MediaType
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*

@ExtendWith(SpringExtension::class)
@WebMvcTest(ClientApplicationEndPoint::class)
class ClientApplicationEndPointTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var redisTemplate: RedisTemplate<*, *>

    @MockBean
    lateinit var jwtDecoder: JwtDecoder

    @MockBean
    lateinit var accountUserDetailsService: AccountUserDetailsService

    @MockBean
    lateinit var clientApplicationRepository: ClientApplicationRepository

    @MockBean
    lateinit var storeClientApplication: StoreClientApplication

    @MockBean
    lateinit var readClientApplication: ReadClientApplication

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Test
    @WithMockUser(authorities = ["VAUTHENTICATOR_ADMIN"])
    fun `store a new client app`() {
        val clientApplication = aClientApp(ClientAppId("clientAppId"))
        val representation = ClientAppRepresentation.fromDomainToRepresentation(clientApplication, storePassword = true)

        mockMvc.perform(
            put("/api/client-applications/clientAppId")
                .with(csrf())
                .content(objectMapper.writeValueAsString(representation))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNoContent)

        verify(storeClientApplication).store(clientApplication, true)
    }

    @Test
    @WithMockUser(authorities = ["VAUTHENTICATOR_ADMIN"])
    fun `reset password for a client app`() {
        mockMvc.perform(
            patch("/api/client-applications/clientApp")
                .with(csrf())

                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mapOf("secret" to "secret")))
        )
            .andExpect(status().isNoContent)

        verify(storeClientApplication).resetPassword(ClientAppId("clientApp"), Secret("secret"))
    }

    @Test
    @WithMockUser(authorities = ["VAUTHENTICATOR_ADMIN"])
    fun `reset password for a not existing client app`() {
        given(storeClientApplication.resetPassword(ClientAppId("clientApp"), Secret("secret")))
            .willThrow(ClientApplicationNotFound("the client application clientApp was not found"))

        mockMvc.perform(
            patch("/api/client-applications/clientApp")
                .with(csrf())

                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mapOf("secret" to "secret")))
        )
            .andExpect(status().isNotFound)

    }

    @Test
    @WithMockUser(authorities = ["VAUTHENTICATOR_ADMIN"])
    fun `view all client app`() {
        val clientApplication = aClientApp(ClientAppId("clientApp"))
        val body = listOf(
            ClientAppInListRepresentation.fromDomainToRepresentation(clientApplication),
            ClientAppInListRepresentation.fromDomainToRepresentation(clientApplication),
            ClientAppInListRepresentation.fromDomainToRepresentation(clientApplication)
        )

        given(readClientApplication.findAll())
            .willReturn(listOf(clientApplication, clientApplication, clientApplication))

        mockMvc.perform(get("/api/client-applications"))
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(body)))

        verify(readClientApplication).findAll()
    }

    @Test
    @WithMockUser(authorities = ["VAUTHENTICATOR_ADMIN"])
    fun `view a specific client app`() {
        val clientApplication = aClientApp(ClientAppId("clientApp"))
        val body = ClientAppRepresentation.fromDomainToRepresentation(clientApplication)

        given(readClientApplication.findOne(ClientAppId("clientApp")))
            .willReturn(Optional.of(aClientApp(ClientAppId("clientApp"))))

        mockMvc.perform(get("/api/client-applications/clientApp"))
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(body)))

        verify(readClientApplication).findOne(ClientAppId("clientApp"))
    }

    @Test
    @WithMockUser(authorities = ["VAUTHENTICATOR_ADMIN"])
    fun `delete a specific client app`() {

        mockMvc.perform(
            delete("/api/client-applications/clientApp")
                .with(csrf())
        )
            .andExpect(status().isNoContent)

        verify(clientApplicationRepository).delete(ClientAppId("clientApp"))
    }
}