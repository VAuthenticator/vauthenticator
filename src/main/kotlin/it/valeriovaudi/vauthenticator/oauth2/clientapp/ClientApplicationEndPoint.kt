package it.valeriovaudi.vauthenticator.oauth2.clientapp

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.function.router

@Configuration
class ClientApplicationEndPoint {

    @Bean
    fun clientApplicationsEndPointRoutes() =
            router {
//                GET("/api/client-applications")
//                GET("/api/client-applications/{clientAppId}")
//                PUT("/api/client-applications/{clientAppId}")
//                DELETE("/api/client-applications/{clientAppId}")
            }

}
