package com.vauthenticator.config

import com.vauthenticator.account.mailverification.SendVerifyMailChallenge
import com.vauthenticator.account.repository.AccountRepository
import com.vauthenticator.account.signup.SignUpUseCase
import com.vauthenticator.account.welcome.SayWelcome
import com.vauthenticator.mail.MailSenderService
import com.vauthenticator.oauth2.clientapp.ClientApplicationRepository
import com.vauthenticator.password.VAuthenticatorPasswordEncoder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
class SingUpConfiguration {

    @Bean
    fun signUpUseCase(
        clientAccountRepository: ClientApplicationRepository,
        accountRepository: AccountRepository,
        welcomeMailSender: MailSenderService,
        sendVerifyMailChallenge: SendVerifyMailChallenge,
        vAuthenticatorPasswordEncoder: VAuthenticatorPasswordEncoder,
        sayWelcome: SayWelcome,
    ): SignUpUseCase =
        SignUpUseCase(
            clientAccountRepository,
            accountRepository,
            sendVerifyMailChallenge,
            vAuthenticatorPasswordEncoder,
            sayWelcome
        )

}