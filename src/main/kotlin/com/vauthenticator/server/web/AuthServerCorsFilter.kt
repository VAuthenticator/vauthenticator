package com.vauthenticator.server.web

import com.vauthenticator.server.oauth2.clientapp.domain.AllowedOrigin
import com.vauthenticator.server.oauth2.clientapp.domain.ClientApplicationRepository
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.filter.OncePerRequestFilter

class AuthServerCorsFilter(
    private val clientApplicationRepository: ClientApplicationRepository
) :
    OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val allowedOrigin = originFrom(request)
        if (clientApplicationRepository.findAll()
                .any { it.allowedOrigins.content.contains(allowedOrigin) }
        ) {
            response.addHeader("Access-Control-Allow-Origin", allowedOrigin.content)
            response.addHeader("Access-Control-Allow-Methods", "GET POST OPTION")
            response.addHeader("Access-Control-Max-Age", "3600")
            response.addHeader("Access-Control-Allow-Credentials", "true")
        }

        filterChain.doFilter(request, response)
    }

    private fun originFrom(request: HttpServletRequest): AllowedOrigin {
        val serverPort = if ((request.serverPort != 80) && (request.serverPort != 443)) ":${request.serverPort}" else ""
        val content = "${request.scheme}://${request.serverName}$serverPort"
        println(content)
        return AllowedOrigin(content)
    }

}