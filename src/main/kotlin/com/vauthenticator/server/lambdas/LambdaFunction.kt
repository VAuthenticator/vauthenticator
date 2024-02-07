package com.vauthenticator.server.lambdas

interface LambdaFunction {

    fun save(name: LambdaFunctionName, content: LambdaFunctionContent, dependencies: LambdaFunctionDependencies): LambdaFunctionId

    fun delete(id: LambdaFunctionId)

    fun execute(id: LambdaFunctionId, context: LambdaFunctionContext): LambdaFunctionContext

}

@JvmInline
value class LambdaFunctionId(val content: String)

typealias LambdaFunctionContent = ByteArray

typealias LambdaFunctionContext = Map<String, String>

data class LambdaFunctionDependency(val name: String, val version: String)

typealias LambdaFunctionDependencies = List<LambdaFunctionDependency>

@JvmInline
value class LambdaFunctionName(val content: String)


fun interface LambdaFunctionContextFactory {
    fun newLambdaFunctionContext(): LambdaFunctionContext
}

class AccessTokenLambdaFunctionContextFactory : LambdaFunctionContextFactory {
    override fun newLambdaFunctionContext(): LambdaFunctionContext {
        return emptyMap()
    }

}