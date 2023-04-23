package com.vauthenticator.server.role

import com.vauthenticator.server.extentions.clientAppId
import com.vauthenticator.server.extentions.hasEnoughScopes
import com.vauthenticator.server.extentions.oauth2ClientId
import com.vauthenticator.server.oauth2.clientapp.*
import jakarta.servlet.http.HttpSession
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import java.util.*

class PermissionValidator(private val clientApplicationRepository: ClientApplicationRepository) {

    fun validate(
        principal: JwtAuthenticationToken?,
        session: HttpSession,
        scopes: Scopes
    ) {
        Optional.ofNullable(principal)
            .ifPresentOrElse(
                { principalScopesValidation(it, scopes) },
                { clientAppScopesValidation(session, scopes) }
            )
    }

    private fun clientAppScopesValidation(
        session: HttpSession,
        scopes: Scopes
    ) {
        session.oauth2ClientId()
            .ifPresentOrElse({
                clientApplicationRepository.findOne(it)
                    .ifPresent {
                        if (!it.hasEnoughScopes(scopes)) {
                            throw InsufficientClientApplicationScopeException("The client app ${it.clientAppId.content} does not support reset-password use case........ consider to add ${Scope.RESET_PASSWORD.content} as scope")
                        }
                    }

            }, { throw ClientApplicationNotFound("no client app found") })
    }

    private fun principalScopesValidation(
        it: JwtAuthenticationToken,
        scopes: Scopes
    ) {
        if (!it.hasEnoughScopes(scopes)) {
            throw InsufficientClientApplicationScopeException("The client app ${it.clientAppId().content} does not support reset-password use case........ consider to add ${Scope.RESET_PASSWORD.content} as scope")
        }
    }
}