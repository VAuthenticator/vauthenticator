package com.vauthenticator.server.web

import com.vauthenticator.server.oauth2.clientapp.domain.AllowedOrigin
import com.vauthenticator.server.oauth2.clientapp.domain.AllowedOriginRepository
import com.vauthenticator.server.oauth2.clientapp.domain.ClientApplicationRepository
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.filter.OncePerRequestFilter
import software.amazon.awssdk.utils.Logger.loggerFor

class AuthServerCorsFilter(
    private val allowedOriginRepository: AllowedOriginRepository
) : OncePerRequestFilter() {

    private val logger = loggerFor(AuthServerCorsFilter::class.java)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val allowedOrigin = originFrom(request)
        logger.info { "allowedOrigin" }
        logger.info { allowedOrigin.toString() }
        allowedOrigin?.let {
            if (allowedOriginRepository.getAllAvailableAllowedOrigins().contains(allowedOrigin)) {
                response.addHeader("Access-Control-Allow-Origin", allowedOrigin.content)
                response.addHeader("Access-Control-Allow-Methods", "GET POST OPTION")
                response.addHeader("Access-Control-Max-Age", "3600")
                response.addHeader("Access-Control-Allow-Credentials", "true")
            }
        }

        filterChain.doFilter(request, response)
    }

    private fun originFrom(request: HttpServletRequest): AllowedOrigin? {
        return request.getHeader("Origin")?.let { AllowedOrigin(it) }
    }

}

