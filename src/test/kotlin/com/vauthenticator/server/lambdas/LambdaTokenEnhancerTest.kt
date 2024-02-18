package com.vauthenticator.server.lambdas

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.JwsHeader
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext

@ExtendWith(MockKExtension::class)
class LambdaTokenEnhancerTest {

    private val input = JwtEncodingContext.with(
        JwsHeader.with(MacAlgorithm.HS256),
        JwtClaimsSet.builder()
    )
        .tokenType(OAuth2TokenType.ACCESS_TOKEN)
        .registeredClient(
            RegisteredClient.withId("client_id")
                .clientId("client_id")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("http://localhost/calback")
                .build()
        )
        .build()

    private val lambdaFunctionContextFactory: LambdaFunctionContextFactory<JwtEncodingContext> =
        AwsLambdaFunctionContextFactory()

    @MockK
    private lateinit var lambdaFunction: LambdaFunction

    @Test
    fun `when the lambda is not applied`() {
        val uut = LambdaTokenEnhancer(false, "A_LAMBDA_NAME", lambdaFunction, lambdaFunctionContextFactory)

        uut.customize(input)

        verify(exactly = 0) { lambdaFunction.execute(any(), any()) }
    }

    @Test
    fun `when the lambda is applied`() {
        val uut = LambdaTokenEnhancer(true, "A_LAMBDA_NAME", lambdaFunction, lambdaFunctionContextFactory)

        every { lambdaFunction.execute(LambdaFunctionId("A_LAMBDA_NAME"), lambdaFunctionContextFactory.newLambdaFunctionContext(input)) } returns lambdaFunctionContextFactory.newLambdaFunctionContext(input)

        uut.customize(input)

        verify(exactly = 1) { lambdaFunction.execute(LambdaFunctionId("A_LAMBDA_NAME"), lambdaFunctionContextFactory.newLambdaFunctionContext(input)) }
    }
}