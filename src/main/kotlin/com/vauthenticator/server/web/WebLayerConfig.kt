package com.vauthenticator.server.web

import com.vauthenticator.server.oauth2.clientapp.domain.ClientApplicationRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
class WebLayerConfig {

    @Bean
    fun authServerCorsFilter(clientApplicationRepository: ClientApplicationRepository) =
        AuthServerCorsFilter(clientApplicationRepository)

    @Bean
    fun springCurrentHttpServletRequestService() =
        SpringCurrentHttpServletRequestService()
}