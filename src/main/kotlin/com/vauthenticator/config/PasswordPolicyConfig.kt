package com.vauthenticator.config

import com.vauthenticator.password.*
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(PasswordPolicyConfigProp::class)
class PasswordPolicyConfig {

    @Bean
    fun passwordPolicy(passwordPolicyConfigProp: PasswordPolicyConfigProp) =
        CompositePasswordPolicy(
            setOf(
                MinimumCharacterPasswordPolicy(passwordPolicyConfigProp.passwordMinSize),
                SpecialCharacterPasswordPolicy(passwordPolicyConfigProp.passwordMinSpecialSymbol)
            )
        )
}