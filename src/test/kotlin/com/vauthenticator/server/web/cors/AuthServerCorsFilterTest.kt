package com.vauthenticator.server.web.cors

import com.vauthenticator.server.oauth2.clientapp.domain.ClientApplicationRepository
import com.vauthenticator.server.support.ClientAppFixture
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import jakarta.servlet.FilterChain
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import kotlin.test.Test

@ExtendWith(MockKExtension::class)
class AuthServerCorsFilterTest {

    @MockK
    lateinit var clientApplicationRepository: ClientApplicationRepository

    @MockK
    lateinit var corsConfigurationResolver: CorsConfigurationResolver

    @MockK
    lateinit var filterChain: FilterChain

    val clientAppId = ClientAppFixture.aClientAppId()

    lateinit var uut: AuthServerCorsFilter

    @BeforeEach
    fun setUp() {
        uut = AuthServerCorsFilter(corsConfigurationResolver)
    }

    @Test
    fun `when the origin is allowed`() {
        val origin = "http://example.com"
        val request = requestFrom(origin)
        val response = MockHttpServletResponse()

        every { corsConfigurationResolver.configurationFor(any()) } returns AuthServerCorsConfiguration(origin)
        every { filterChain.doFilter(request, response) } just runs

        uut.doFilter(request, response, filterChain)

        assertTrue { response.getHeaders("Access-Control-Allow-Origin").contains(origin) }
        assertTrue { response.getHeaders("Access-Control-Allow-Methods").contains("GET POST OPTIONS") }
        assertTrue { response.getHeaders("Access-Control-Max-Age").contains("3600") }
        assertTrue { response.getHeaders("Access-Control-Allow-Credentials").contains("true") }

        verify { filterChain.doFilter(request, response) }
    }

    private fun requestFrom(origin: String): MockHttpServletRequest {
        val request = MockHttpServletRequest()
        request.method = "GET"
        request.addHeader("Origin", origin)
        return request
    }


}