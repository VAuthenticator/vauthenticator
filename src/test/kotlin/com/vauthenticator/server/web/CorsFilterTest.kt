package com.vauthenticator.server.web

import com.vauthenticator.server.oauth2.clientapp.domain.ClientApplicationRepository
import com.vauthenticator.server.support.ClientAppFixture.aClientApp
import com.vauthenticator.server.support.ClientAppFixture.aClientAppId
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.mock.web.MockFilterChain
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import java.util.Optional

@ExtendWith(MockKExtension::class)
class CorsFilterTest {

    @MockK
    lateinit var clientApplicationRepository: ClientApplicationRepository

    @Test
    fun `when the client app aware endpoints are allowed`() {
        val clientAppId = aClientAppId()
        every { clientApplicationRepository.findOne(clientAppId) } returns Optional.of(aClientApp(clientAppId = clientAppId))

        val uut = CorsFilter(clientApplicationRepository)

        val request = MockHttpServletRequest()
        request.method = "POST"
        request.requestURI = "/token"
        request.remoteHost = "example.com"
        request.addParameter("client_id", clientAppId.content)

        val response = MockHttpServletResponse()
        val filterChain = MockFilterChain()

        uut.doFilter(request, response, filterChain)

        assertTrue { response.getHeaders("Access-Control-Allow-Origin").contains("example.com") }
        assertTrue { response.getHeaders("Access-Control-Allow-Methods").contains("GET POST PUT DELETE OPTION") }
        assertTrue { response.getHeaders("Access-Control-Max-Age").contains("3600") }
        assertTrue { response.getHeaders("Access-Control-Allow-Credentials").contains("true") }
    }
}