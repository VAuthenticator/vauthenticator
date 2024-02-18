package com.vauthenticator.server.lambdas

import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer



class LambdaTokenEnhancer(
    private val enabled: Boolean,
    private val lambdaName : String,
    private val lambdaFunction: LambdaFunction,
    private val lambdaFunctionContextFactory: LambdaFunctionContextFactory<JwtEncodingContext>
) : OAuth2TokenCustomizer<JwtEncodingContext> {


    override fun customize(context: JwtEncodingContext) {
        if (enabled) {
            val tokenType = context.tokenType.value

            val lambdaFunctionId = LambdaFunctionId(lambdaName)
            val lambdaFunctionContext = lambdaFunctionContextFactory.newLambdaFunctionContext(context)

            val execute = lambdaFunction.execute(lambdaFunctionId, lambdaFunctionContext)
            if ("id_token" === tokenType) {
                (execute.content["id_token_claims"] as Map<String, String>)
                    .map { claim -> context.claims.claim(claim.key, claim.value) }

            } else if ("access_token" === tokenType) {
                (execute.content["access_token_claims"] as Map<String, String>)
                    .map { claim -> context.claims.claim(claim.key, claim.value) }
            }
        }
    }
}

class AwsLambdaFunctionContextFactory : LambdaFunctionContextFactory<JwtEncodingContext> {
    override fun newLambdaFunctionContext(input: JwtEncodingContext): LambdaFunctionContext {
        val clientId = input.registeredClient.clientId

        val generalContext = mutableMapOf<String, String>()
        generalContext["client_id"] = clientId

        val accessTokenContext = mutableMapOf<String, String>()
        val idTokenContext = mutableMapOf<String, String>()

        return LambdaFunctionContext(
            mapOf(
                "general_context_claims" to generalContext,
                "access_token_claims" to accessTokenContext,
                "id_token_claims" to idTokenContext
            )
        )
    }

}