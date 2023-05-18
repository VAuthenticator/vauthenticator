package com.vauthenticator.server.role

import com.vauthenticator.server.extentions.clientAppId
import com.vauthenticator.server.extentions.hasEnoughScopes
import com.vauthenticator.server.extentions.oauth2ClientId
import com.vauthenticator.server.oauth2.clientapp.ClientApplicationNotFound
import com.vauthenticator.server.oauth2.clientapp.ClientApplicationRepository
import com.vauthenticator.server.oauth2.clientapp.InsufficientClientApplicationScopeException
import com.vauthenticator.server.oauth2.clientapp.Scopes
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
            .ifPresentOrElse({ clientAppId ->
                clientApplicationRepository.findOne(clientAppId)
                    .ifPresent { clientApplication ->
                        if (!clientApplication.hasEnoughScopes(scopes)) {
                            throw InsufficientClientApplicationScopeException("The client app ${clientApplication.clientAppId.content} does not support this use case........ consider to add ${scopes.content.map { it.content }} as scope")
                        }
                    }

            }, { throw ClientApplicationNotFound("no client app found") })
    }

    private fun principalScopesValidation(
        it: JwtAuthenticationToken,
        scopes: Scopes
    ) {
        if (!it.hasEnoughScopes(scopes)) {
            throw InsufficientClientApplicationScopeException("The client app ${it.clientAppId().content} does not support this use case........ consider to add ${scopes.content.map { it.content }} as scope")
        }
    }
}