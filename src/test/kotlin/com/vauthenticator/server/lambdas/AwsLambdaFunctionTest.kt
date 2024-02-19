package com.vauthenticator.server.lambdas

import com.fasterxml.jackson.databind.ObjectMapper
import com.vauthenticator.server.extentions.toSha256
import com.vauthenticator.server.support.JwtEncodingContextFixture.newContext
import com.vauthenticator.server.support.RequestAttributesFixture.requestAttributes
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.redis.core.HashOperations
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext
import org.springframework.web.context.request.RequestContextHolder
import software.amazon.awssdk.core.SdkBytes
import software.amazon.awssdk.services.lambda.LambdaClient
import software.amazon.awssdk.services.lambda.model.InvokeRequest
import software.amazon.awssdk.services.lambda.model.InvokeResponse

@ExtendWith(MockKExtension::class)
class AwsLambdaFunctionTest {

    @MockK
    private lateinit var redisTemplate: RedisTemplate<String, String>

    @MockK
    private lateinit var hashOperations: HashOperations<String, String, String>

    private val objectMapper: ObjectMapper = ObjectMapper()

    @MockK
    private lateinit var client: LambdaClient

    private val lambdaFunctionContextFactory: LambdaFunctionContextFactory<JwtEncodingContext> =
        AwsLambdaFunctionContextFactory()
    @Test
    fun `when the lambda invocation is cached`() {
        val context = lambdaFunctionContextFactory.newLambdaFunctionContext(newContext)

        RequestContextHolder.setRequestAttributes(requestAttributes)
        val sessionId = requestAttributes.sessionId

        every { redisTemplate.opsForHash<String, String>() } returns hashOperations
        every {
            hashOperations.get(
                sessionId,
                sessionId.toSha256()
            )
        } returns objectMapper.writeValueAsString(context.content)

        val uut = AwsLambdaFunction(redisTemplate, objectMapper, client)

        val actual = uut.execute(LambdaFunctionId("A_LAMBDA_FUNCTION_ID"), context)

        assertEquals(context, actual)
        verify(exactly = 0) { client.invoke(any<InvokeRequest>()) }
    }

    @Test
    fun `when the lambda invocation is NOT cached`() {
        val context = lambdaFunctionContextFactory.newLambdaFunctionContext(newContext)
        val stringSerializedContext = objectMapper.writeValueAsString(context.content)
        val invokeRequest: InvokeRequest = InvokeRequest.builder()
            .functionName("A_LAMBDA_FUNCTION_ID")
            .payload(SdkBytes.fromUtf8String(stringSerializedContext))
            .build()
        val invokeResponse = InvokeResponse.builder().payload(SdkBytes.fromUtf8String(stringSerializedContext)).build()

        RequestContextHolder.setRequestAttributes(requestAttributes)
        val sessionId = requestAttributes.sessionId

        every { redisTemplate.opsForHash<String, String>() } returns hashOperations
        every {
            hashOperations.get(
                sessionId,
                sessionId.toSha256()
            )
        } returns null

        every { client.invoke(invokeRequest) } returns invokeResponse
        every { hashOperations.put(sessionId, sessionId.toSha256(), stringSerializedContext) } just runs

        val uut = AwsLambdaFunction(redisTemplate, objectMapper, client)

        val actual = uut.execute(LambdaFunctionId("A_LAMBDA_FUNCTION_ID"), context)

        assertEquals(context, actual)
        verify(exactly = 1) { client.invoke(any<InvokeRequest>()) }
    }
}
