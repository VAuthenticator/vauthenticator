package com.vauthenticator.server.config

import com.vauthenticator.server.password.CompositePasswordPolicy
import com.vauthenticator.server.password.MinimumCharacterPasswordPolicy
import com.vauthenticator.server.password.PasswordPolicyConfigProp
import com.vauthenticator.server.password.SpecialCharacterPasswordPolicy
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