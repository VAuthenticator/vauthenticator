package com.vauthenticator.server.config

import com.vauthenticator.server.password.PasswordGenerator
import com.vauthenticator.server.password.PasswordGeneratorCriteria
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@EnableConfigurationProperties(PasswordGeneratorCriteria::class)
@Configuration(proxyBeanMethods = false)
class PasswordGeneratorConfig {


    @Bean
    fun passwordGenerator(passwordGeneratorCriteria: PasswordGeneratorCriteria) = PasswordGenerator(passwordGeneratorCriteria)
}