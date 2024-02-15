package com.vauthenticator.server.lambdas

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import software.amazon.awssdk.core.SdkBytes
import software.amazon.awssdk.services.lambda.LambdaClient
import software.amazon.awssdk.services.lambda.model.InvokeRequest

interface LambdaFunction {

    fun save(
        name: LambdaFunctionName,
        content: LambdaFunctionContent,
        dependencies: LambdaFunctionDependencies
    ): LambdaFunctionId

    fun delete(id: LambdaFunctionId)

    fun execute(id: LambdaFunctionId, context: LambdaFunctionContext): LambdaFunctionContext

}

@JvmInline
value class LambdaFunctionId(val content: String)

typealias LambdaFunctionContent = ByteArray

@JvmInline
value class LambdaFunctionContext(val content: Map<String, Any>)

data class LambdaFunctionDependency(val name: String, val version: String)

typealias LambdaFunctionDependencies = List<LambdaFunctionDependency>

@JvmInline
value class LambdaFunctionName(val content: String)


fun interface LambdaFunctionContextFactory<T> {
    fun newLambdaFunctionContext(input: T): LambdaFunctionContext
}

@Component
class AwsLambdaFunction(
    private val objectMapper: ObjectMapper,
    private val client: LambdaClient
) : LambdaFunction {
    override fun save(
        name: LambdaFunctionName,
        content: LambdaFunctionContent,
        dependencies: LambdaFunctionDependencies
    ): LambdaFunctionId {
        TODO("Not yet implemented")
    }

    override fun delete(id: LambdaFunctionId) {
        TODO("Not yet implemented")
    }

    override fun execute(id: LambdaFunctionId, context: LambdaFunctionContext): LambdaFunctionContext {
        val typeRef: TypeReference<Map<String, Any>> = object : TypeReference<Map<String, Any>>() {}

        val invokeRequest: InvokeRequest = InvokeRequest.builder()
            .functionName(id.content)
            .payload(SdkBytes.fromUtf8String(objectMapper.writeValueAsString(context.content)))
            .build()

        val invoke = client.invoke(invokeRequest)
        return LambdaFunctionContext(objectMapper.readValue(invoke.payload().asByteArray(), typeRef))
    }

}