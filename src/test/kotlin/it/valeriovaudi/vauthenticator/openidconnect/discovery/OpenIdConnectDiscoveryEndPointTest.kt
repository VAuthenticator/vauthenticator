package it.valeriovaudi.vauthenticator.openidconnect.discovery

import it.valeriovaudi.vauthenticator.TestAdditionalConfiguration
import it.valeriovaudi.vauthenticator.openidconnect.discovery.OpenIdConnectDiscovery.Companion.newOpenIdConnectDiscovery
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper

@RunWith(SpringRunner::class)
@WebMvcTest(OpenIdConnectDiscoveryEndPoint::class)
@Import(TestAdditionalConfiguration::class)
@TestPropertySource(properties = ["auth.oidcIss=asIssuer"])
class OpenIdConnectDiscoveryEndPointTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun `happy path`() {
        val objectMapper = ObjectMapper()
        println(mockMvc.perform(get("/.well-known/openid-configuration"))
                .andReturn().response.contentAsString)

        mockMvc.perform(get("/.well-known/openid-configuration"))
                .andExpect(content().json(objectMapper.writeValueAsString(newOpenIdConnectDiscovery("anIssuer"))))
    }
}