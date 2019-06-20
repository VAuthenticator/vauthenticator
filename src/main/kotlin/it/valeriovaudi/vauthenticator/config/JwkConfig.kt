package it.valeriovaudi.vauthenticator.config

import it.valeriovaudi.vauthenticator.jwk.JwkFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JwkConfig {

    @Bean
    fun jwkFactory() = JwkFactory()
}