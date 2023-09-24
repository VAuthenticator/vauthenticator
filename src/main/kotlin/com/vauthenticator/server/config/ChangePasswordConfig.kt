package com.vauthenticator.server.config

import com.vauthenticator.server.account.repository.AccountRepository
import com.vauthenticator.server.password.PasswordPolicy
import com.vauthenticator.server.password.VAuthenticatorPasswordEncoder
import com.vauthenticator.server.password.changepassword.ChangePassword
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
class ChangePasswordConfig {

    @Bean
    fun changePassword(
        passwordPolicy: PasswordPolicy,
        passwordEncoder: VAuthenticatorPasswordEncoder,
        accountRepository: AccountRepository
    ) =
        ChangePassword(passwordPolicy, passwordEncoder, accountRepository)
}