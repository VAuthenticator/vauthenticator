package com.vauthenticator.server.web

import com.vauthenticator.server.oauth2.clientapp.domain.ClientAppId
import com.vauthenticator.server.oauth2.clientapp.domain.ClientApplicationRepository
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings
import org.springframework.web.filter.OncePerRequestFilter

class AuthServerCorsFilter(
    private val authorizationServerSettings: AuthorizationServerSettings,
    private val clientApplicationRepository: ClientApplicationRepository
) :
    OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        if (request.requestURI.startsWith(authorizationServerSettings.tokenEndpoint) || request.requestURI.startsWith(authorizationServerSettings.authorizationEndpoint)) {
            val clientId = if (request.contentType == MediaType.APPLICATION_FORM_URLENCODED_VALUE) {
                request.reader.readText().split("&").find { it.startsWith("client_id=") }?.split("=")?.last()
            } else {
                request.parameterMap["client_id"]?.last()
            }

            if (clientId != null && !clientApplicationRepository.findOne(ClientAppId(clientId)).isEmpty) {
                request.remoteHost.let { response.addHeader("Access-Control-Allow-Origin", it) }
                response.addHeader("Access-Control-Allow-Methods", "GET POST OPTION")
                response.addHeader("Access-Control-Max-Age", "3600")
                response.addHeader("Access-Control-Allow-Credentials", "true")
            }

        }

        filterChain.doFilter(request, response)
    }
}