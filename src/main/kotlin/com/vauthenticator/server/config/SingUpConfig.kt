package com.vauthenticator.server.config

import com.vauthenticator.server.account.repository.AccountRepository
import com.vauthenticator.server.account.signup.SignUpUse
import com.vauthenticator.server.events.VAuthenticatorEventsDispatcher
import com.vauthenticator.server.oauth2.clientapp.ClientApplicationRepository
import com.vauthenticator.server.password.PasswordPolicy
import com.vauthenticator.server.password.VAuthenticatorPasswordEncoder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
class SingUpConfig {

    @Bean
    fun signUpUseCase(
        passwordPolicy: PasswordPolicy,
        clientAccountRepository: ClientApplicationRepository,
        accountRepository: AccountRepository,
        vAuthenticatorPasswordEncoder: VAuthenticatorPasswordEncoder,
        vAuthenticatorEventsDispatcher : VAuthenticatorEventsDispatcher
    ): SignUpUse =
        SignUpUse(
            passwordPolicy,
            clientAccountRepository,
            accountRepository,
            vAuthenticatorPasswordEncoder,
            vAuthenticatorEventsDispatcher
        )

}