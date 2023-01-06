package com.vauthenticator.config

import com.vauthenticator.password.CompositePasswordPolicy
import com.vauthenticator.password.MinimumCharacterPasswordPolicy
import com.vauthenticator.password.PasswordPolicyConfigProp
import com.vauthenticator.password.SpecialCharacterPasswordPolicy
import it.valeriovaudi.vauthenticator.password.*
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