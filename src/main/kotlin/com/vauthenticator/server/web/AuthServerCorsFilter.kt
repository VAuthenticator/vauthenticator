package com.vauthenticator.server.web

import com.vauthenticator.server.oauth2.clientapp.domain.AllowedOrigin
import com.vauthenticator.server.oauth2.clientapp.domain.AllowedOriginRepository
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.web.filter.OncePerRequestFilter

class AuthServerCorsFilter(
    private val allowedOriginRepository: AllowedOriginRepository
) : OncePerRequestFilter() {

    private val logger = LoggerFactory.getLogger(AuthServerCorsFilter::class.java)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val allowedOrigin = originFrom(request)
        logger.info("allowedOrigin")
        logger.info(allowedOrigin.toString())
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
        val header = request.getHeader("Origin")
        println("Origin header")
        println(header)
        return header?.let { AllowedOrigin(it) }
    }

}