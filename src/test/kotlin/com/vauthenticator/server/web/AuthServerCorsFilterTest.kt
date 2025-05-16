package com.vauthenticator.server.web

import com.vauthenticator.server.oauth2.clientapp.domain.AllowedOrigin
import com.vauthenticator.server.oauth2.clientapp.domain.AllowedOrigins
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
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.web.util.UriComponentsBuilder

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
        uut = AuthServerCorsFilter(clientApplicationRepository)
    }

    @ParameterizedTest
    @ValueSource(strings = ["https://example.com","http://example.com", "http://local.example.com:9090"]) // six numbers
    fun `when the origin is allowed`(origin: String) {
        val request = requestFrom(origin)
        val response = MockHttpServletResponse()

        val clientApplication = aClientApp(clientAppId = clientAppId).copy(
            allowedOrigins = AllowedOrigins(
                setOf(
                    AllowedOrigin(origin)
                )
            )
        )

        every { clientApplicationRepository.findAll() } returns listOf(clientApplication)
        every { filterChain.doFilter(request, response) } just runs

        uut.doFilter(request, response, filterChain)

        assertionsFor(request, response, origin)
    }

    private fun requestFrom(origin: String): MockHttpServletRequest {
        val request = MockHttpServletRequest()
        val uriComponents = UriComponentsBuilder.fromUriString(origin).build()
        request.method = "GET"
        request.scheme = uriComponents.scheme.toString()
        request.serverName = uriComponents.host!!

        when (request.scheme) {
            "http" -> {
                if (uriComponents.port == -1) {
                    request.serverPort = 80
                } else {
                    request.serverPort = uriComponents.port
                }
            }

            "https" -> request.serverPort = 443
        }
        return request
    }


    private fun assertionsFor(request: HttpServletRequest, response: HttpServletResponse, origin: String) {
        assertTrue { response.getHeaders("Access-Control-Allow-Origin").contains(origin) }
        assertTrue { response.getHeaders("Access-Control-Allow-Methods").contains("GET POST OPTION") }
        assertTrue { response.getHeaders("Access-Control-Max-Age").contains("3600") }
        assertTrue { response.getHeaders("Access-Control-Allow-Credentials").contains("true") }

        verify { filterChain.doFilter(request, response) }
    }

}