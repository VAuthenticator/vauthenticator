package com.vauthenticator.server.oauth2.token

import com.vauthenticator.server.keys.KeyRepository
import com.vauthenticator.server.keys.Kid
import com.vauthenticator.server.lambdas.LambdaFunction
import com.vauthenticator.server.lambdas.LambdaFunctionContextFactory
import com.vauthenticator.server.lambdas.LambdaFunctionId
import com.vauthenticator.server.oauth2.clientapp.ClientAppId
import com.vauthenticator.server.oauth2.clientapp.ClientApplicationRepository
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer
import java.util.stream.Collectors

class OAuth2TokenEnhancer(
    private val lambdaFunction: LambdaFunction,
    private val lambdaFunctionContextFactory: LambdaFunctionContextFactory,
    private val assignedKeys: MutableSet<Kid>,
    private val keyRepository: KeyRepository,
    private val clientApplicationRepository: ClientApplicationRepository
) : OAuth2TokenCustomizer<JwtEncodingContext> {
    override fun customize(context: JwtEncodingContext) {
        val tokenType = context.tokenType.value
        if ("access_token" == tokenType) {

            val signatureKey = keyRepository.signatureKeys().peekOneAtRandomWithout(assignedKeys)
            context.jwsHeader.keyId(signatureKey.kid.content())
            val newLambdaFunctionContext = lambdaFunctionContextFactory.newLambdaFunctionContext()
            val lambdaFunctionContext =
                lambdaFunction.execute(LambdaFunctionId("OAuth2TokenEnhancer"), newLambdaFunctionContext)

            lambdaFunctionContext.map {
                context.claims.claim(it.key, it.value)
            }

            if (context.authorizationGrantType.equals(AuthorizationGrantType.CLIENT_CREDENTIALS)) {
                val clientId = context.registeredClient.clientId
                val findOne = clientApplicationRepository.findOne(ClientAppId(clientId))
                findOne.ifPresent {
                    context.claims.claim("user_name", it.clientAppId.content)
                    context.claims.claim(
                        "authorities",
                        it.authorities.content.stream().map { authority -> authority.content }
                            .collect(Collectors.toList())
                    )
                }
            } else {
                val attributes = context.authorization!!.attributes
                val principal = attributes["java.security.Principal"] as Authentication

                context.claims.claim("user_name", principal.name)
                context.claims.claim("authorities", principal.authorities
                    .stream()
                    .map { obj: GrantedAuthority -> obj.authority }
                    .collect(Collectors.toList()))
            }

        }
    }
}