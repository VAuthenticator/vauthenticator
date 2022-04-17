package it.valeriovaudi.vauthenticator.proxy

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.options
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpMethod
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.web.client.RestTemplate
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.servlet.HandlerMapping

internal class VAuthenticatorApiProxyTest {

    var wireMockServer: WireMockServer = WireMockServer(options().dynamicHttpsPort()) //No-args constructor will start on port 8080, no HTTPS

    @BeforeEach
    fun setUp() {
        wireMockServer.start()
    }

    @AfterEach
    fun tearDown() {
        wireMockServer.stop()
    }

    @Test
    internal fun `do a service call without body`() {
        stubFor(
                get("/api/something")
                        .willReturn(
                                ok("echo")
                        )
        )

        val apiProxy = VAuthenticatorApiProxy("http://localhost:${wireMockServer.port()}/api",
                ApiServiceCallProxyService("/secure/api"),
                RestTemplate())

        val mockHttpServletRequest = MockHttpServletRequest()
        mockHttpServletRequest.setAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE, "/secure/api/something")
        mockHttpServletRequest.addHeader("Content-Type", "text/html")
        val servletWebRequest = ServletWebRequest(mockHttpServletRequest, MockHttpServletResponse())
        val proxy = apiProxy.proxy(servletWebRequest, HttpMethod.GET, null)
        val body: ByteArray = proxy.body!!
        Assertions.assertEquals("echo", String(body))
    }

    @Test
    internal fun `do a service call with body`() {
        stubFor(
                post("/api/something")
                        .withRequestBody(equalTo("hello"))
                        .willReturn(
                                ok("echo")
                        )
        )

        val apiProxy = VAuthenticatorApiProxy("http://localhost:${wireMockServer.port()}/api",
                ApiServiceCallProxyService("/secure/api"),
                RestTemplate())

        val mockHttpServletRequest = MockHttpServletRequest()
        mockHttpServletRequest.setAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE, "/secure/api/something")
        mockHttpServletRequest.addHeader("Content-Type", "text/html")
        val servletWebRequest = ServletWebRequest(mockHttpServletRequest, MockHttpServletResponse())
        val proxy = apiProxy.proxy(servletWebRequest, HttpMethod.POST, "hello")
        val body: ByteArray = proxy.body!!
        Assertions.assertEquals("echo", String(body))
    }
}