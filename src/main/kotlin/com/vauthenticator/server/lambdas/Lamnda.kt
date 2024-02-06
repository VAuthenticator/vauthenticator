package com.vauthenticator.server.lambdas

interface Lamnda {

    fun save(name: LambdaName, content: LambdaContent, dependencies: LambdaDependencies): LambdaFunctionId

    fun delete(id: LambdaFunctionId)

    fun execute(id: LambdaFunctionId, context: LambdaContext): LambdaContext

}

@JvmInline
value class LambdaFunctionId(val content: String)

typealias LambdaContent = ByteArray

typealias LambdaContext = Map<String, String>

data class LambdaDependency(val name: String, val version: String)

typealias LambdaDependencies = List<LambdaDependency>

@JvmInline
value class LambdaName(val content: String)


interface LamndaConxetFactory {
    fun newContext(): LambdaContext
}