package com.vauthenticator.server.web

import com.vauthenticator.server.oauth2.clientapp.domain.AllowedOriginRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order

@Configuration(proxyBeanMethods = false)
class WebLayerConfig {


    @Bean
    @Order(Integer.MIN_VALUE)
    fun authServerCorsFilter(allowedOriginRepository: AllowedOriginRepository) =
        AuthServerCorsFilter( allowedOriginRepository)

    @Bean
    fun springCurrentHttpServletRequestService() =
        SpringCurrentHttpServletRequestService()
}