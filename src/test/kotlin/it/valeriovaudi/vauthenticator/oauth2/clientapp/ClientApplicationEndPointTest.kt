package it.valeriovaudi.vauthenticator.oauth2.clientapp

import com.fasterxml.jackson.databind.ObjectMapper
import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientAppFixture.aClientApp
import it.valeriovaudi.vauthenticator.openid.connect.nonce.NonceStore
import it.valeriovaudi.vauthenticator.security.userdetails.AccountUserDetailsService
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.http.MediaType
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*

@RunWith(SpringRunner::class)
@WebMvcTest(ClientApplicationEndPoint::class)
class ClientApplicationEndPointTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var nonceStore: NonceStore

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
    fun `store a new client app`() {
        val clientApplication = aClientApp(ClientAppId("clientApp"))
        val representation = ClientAppRepresentation.fromDomainToRepresentation(clientApplication).copy(setSecret = true)

        mockMvc.perform(put("/api/client-applications/clientAppId")
                .content(objectMapper.writeValueAsString(representation))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent)

        verify(storeClientApplication).store(clientApplication, true)
    }

    @Test
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
    fun `delete a specific client app`() {

        mockMvc.perform(delete("/api/client-applications/clientApp"))
                .andExpect(status().isNoContent)

        verify(clientApplicationRepository).delete(ClientAppId("clientApp"))
    }

}