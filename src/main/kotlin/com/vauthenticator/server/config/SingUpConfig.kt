package com.vauthenticator.server.config

import com.vauthenticator.server.account.mailverification.SendVerifyMailChallenge
import com.vauthenticator.server.account.repository.AccountRepository
import com.vauthenticator.server.account.signup.SignUpUse
import com.vauthenticator.server.account.welcome.SayWelcome
import com.vauthenticator.server.mail.MailSenderService
import com.vauthenticator.server.oauth2.clientapp.ClientApplicationRepository
import com.vauthenticator.server.password.PasswordPolicy
import com.vauthenticator.server.password.VAuthenticatorPasswordEncoder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
class SingUpConfig {

    @Bean
    fun signUpUseCase(
        clientAccountRepository: ClientApplicationRepository,
        accountRepository: AccountRepository,
        welcomeMailSender: MailSenderService,
        passwordPolicy: PasswordPolicy,
        sendVerifyMailChallenge: SendVerifyMailChallenge,
        vAuthenticatorPasswordEncoder: VAuthenticatorPasswordEncoder,
        sayWelcome: SayWelcome,
    ): SignUpUse =
        SignUpUse(
            passwordPolicy,
            clientAccountRepository,
            accountRepository,
            sendVerifyMailChallenge,
            vAuthenticatorPasswordEncoder,
            sayWelcome
        )

}