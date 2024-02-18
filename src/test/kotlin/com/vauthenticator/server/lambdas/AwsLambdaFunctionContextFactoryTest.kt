package com.vauthenticator.server.lambdas

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext

@ExtendWith(MockKExtension::class)
class AwsLambdaFunctionContextFactoryTest {

    @MockK
    private lateinit var input: JwtEncodingContext

    @MockK
    private lateinit var registeredClient: RegisteredClient

    @Test
    fun `happy path`() {
        val uut = AwsLambdaFunctionContextFactory()

        every { input.registeredClient } returns registeredClient
        every { registeredClient.clientId } returns "client_id"

        val emptyMap = emptyMap<String, Any>()
        val expected = LambdaFunctionContext(
            mapOf(
                "general_context_claims" to mapOf("client_id" to "client_id"),
                "access_token_claims" to emptyMap,
                "id_token_claims" to emptyMap
            )
        )
        val actual = uut.newLambdaFunctionContext(input)

        assertEquals(expected, actual)
    }


}