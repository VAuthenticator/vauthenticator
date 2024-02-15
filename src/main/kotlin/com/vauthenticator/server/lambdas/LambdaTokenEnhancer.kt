package com.vauthenticator.server.lambdas

import org.slf4j.LoggerFactory
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer

class LambdaTokenEnhancer(
    private val lambdaFunction: LambdaFunction,
    private val lambdaFunctionContextFactory: LambdaFunctionContextFactory<JwtEncodingContext>
) : OAuth2TokenCustomizer<JwtEncodingContext> {

    private val logger = LoggerFactory.getLogger(LambdaTokenEnhancer::class.java)

    override fun customize(context: JwtEncodingContext) {
        val tokenType = context.tokenType.value

        logger.debug("LambdaTokenEnhancer executed for token type: $tokenType")


        val lambdaFunctionId = LambdaFunctionId("vauthenticator-token-enhancer")
        val lambdaFunctionContext = lambdaFunctionContextFactory.newLambdaFunctionContext(context)

        val execute = lambdaFunction.execute(lambdaFunctionId, lambdaFunctionContext)
        println(execute)
        if ("id_token" == tokenType) {
            (execute.content["id_token_claims"] as Map<String, String>)
                .map { claim -> context.claims.claim(claim.key, claim.value) }

        } else if ("access_token" === tokenType) {
            (execute.content["access_token_claims"] as Map<String, String>)
                .map { claim -> context.claims.claim(claim.key, claim.value) }
        }
        println(context.claims)
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