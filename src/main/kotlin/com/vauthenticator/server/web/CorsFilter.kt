package com.vauthenticator.server.web

import com.vauthenticator.server.oauth2.clientapp.domain.ClientAppId
import com.vauthenticator.server.oauth2.clientapp.domain.ClientApplicationRepository
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.filter.OncePerRequestFilter

class CorsFilter(private val clientApplicationRepository: ClientApplicationRepository) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val clientId = request.parameterMap["client_id"]?.first()
        if (clientId != null && !clientApplicationRepository.findOne(ClientAppId(clientId)).isEmpty) {
            request.remoteHost.let { response.addHeader("Access-Control-Allow-Origin", it) }
            response.addHeader("Access-Control-Allow-Methods", "GET POST PUT DELETE OPTION")
            response.addHeader("Access-Control-Max-Age", "3600")
            response.addHeader("Access-Control-Allow-Credentials", "true")
        }

        filterChain.doFilter(request, response)
    }
}