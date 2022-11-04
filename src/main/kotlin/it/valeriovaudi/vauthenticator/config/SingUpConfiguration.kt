package it.valeriovaudi.vauthenticator.config

import it.valeriovaudi.vauthenticator.account.mailverification.SendVerifyMailChallenge
import it.valeriovaudi.vauthenticator.account.repository.AccountRepository
import it.valeriovaudi.vauthenticator.account.signup.SignUpUseCase
import it.valeriovaudi.vauthenticator.mail.MailSenderService
import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientApplicationRepository
import it.valeriovaudi.vauthenticator.password.VAuthenticatorPasswordEncoder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
class SingUpConfiguration {

    @Bean
    fun signUpUseCase(clientAccountRepository: ClientApplicationRepository,
                      accountRepository: AccountRepository,
                      welcomeMailSender: MailSenderService,
                      sendVerifyMailChallenge: SendVerifyMailChallenge,
                      vAuthenticatorPasswordEncoder: VAuthenticatorPasswordEncoder
    ): SignUpUseCase =
            SignUpUseCase(clientAccountRepository, accountRepository, welcomeMailSender, sendVerifyMailChallenge, vAuthenticatorPasswordEncoder)

}