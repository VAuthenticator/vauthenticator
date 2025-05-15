package com.vauthenticator.server.web

import com.vauthenticator.server.oauth2.clientapp.domain.ClientApplicationRepository
import com.vauthenticator.server.support.ClientAppFixture.aClientApp
import com.vauthenticator.server.support.ClientAppFixture.aClientAppId
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings.*
import java.util.*

@ExtendWith(MockKExtension::class)
class AuthServerCorsFilterTest {

    @MockK
    lateinit var clientApplicationRepository: ClientApplicationRepository

    @MockK
    lateinit var filterChain: FilterChain

    val clientAppId = aClientAppId()

    lateinit var uut: AuthServerCorsFilter

    @BeforeEach
    fun setUp() {
        uut = AuthServerCorsFilter(builder().issuer("http://localhost").build(),clientApplicationRepository)
    }

    @Test
    fun `when token endpoint get client id from the request parameters`() {
        val request = MockHttpServletRequest()
        val response = MockHttpServletResponse()

        request.method = "POST"
        request.requestURI = "/oauth2/token"
        request.remoteHost = "example.com"
        request.addParameter("client_id", clientAppId.content)

        every { clientApplicationRepository.findOne(clientAppId) } returns Optional.of(aClientApp(clientAppId = clientAppId))
        every { filterChain.doFilter(request, response) } just runs

        uut.doFilter(request, response, filterChain)

        assertionsFor(request, response)
    }

    @Test
    fun `when token endpoint get client id from the request body`() {
        val request = MockHttpServletRequest()
        val response = MockHttpServletResponse()

        request.method = "POST"
        request.requestURI = "/oauth2/token"
        request.remoteHost = "example.com"
        request.contentType = MediaType.APPLICATION_FORM_URLENCODED_VALUE
        request.setContent("client_id=${clientAppId.content}".toByteArray())

        every { clientApplicationRepository.findOne(clientAppId) } returns Optional.of(aClientApp(clientAppId = clientAppId))
        every { filterChain.doFilter(request, response) } just runs

        uut.doFilter(request, response, filterChain)

        assertionsFor(request, response)
    }

    @Test
    fun `when the authorize  endpoint get client id from the request parameter`() {
        val request = MockHttpServletRequest()
        val response = MockHttpServletResponse()

        request.method = "GET"
        request.requestURI = "/oauth2/authorize"
        request.remoteHost = "example.com"
        request.addParameter("client_id", clientAppId.content)

        every { clientApplicationRepository.findOne(clientAppId) } returns Optional.of(aClientApp(clientAppId = clientAppId))
        every { filterChain.doFilter(request, response) } just runs

        uut.doFilter(request, response, filterChain)

        assertionsFor(request, response)
    }

    private fun assertionsFor(request: HttpServletRequest, response: HttpServletResponse) {
        assertTrue { response.getHeaders("Access-Control-Allow-Origin").contains("example.com") }
        assertTrue { response.getHeaders("Access-Control-Allow-Methods").contains("GET POST OPTION") }
        assertTrue { response.getHeaders("Access-Control-Max-Age").contains("3600") }
        assertTrue { response.getHeaders("Access-Control-Allow-Credentials").contains("true") }

        verify { filterChain.doFilter(request, response) }
    }

}