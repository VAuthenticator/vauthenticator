package it.valeriovaudi.vauthenticator.config

import it.valeriovaudi.vauthenticator.account.repository.AccountRepository
import it.valeriovaudi.vauthenticator.account.signup.SignUpConfirmationMailConfiguration
import it.valeriovaudi.vauthenticator.account.signup.SignUpConfirmationMailSender
import it.valeriovaudi.vauthenticator.account.signup.SignUpUseCase
import it.valeriovaudi.vauthenticator.mail.MailSenderService
import it.valeriovaudi.vauthenticator.mail.SimpleMailContextFactory
import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientApplicationRepository
import it.valeriovaudi.vauthenticator.oauth2.clientapp.ReadClientApplication
import it.valeriovaudi.vauthenticator.oauth2.clientapp.StoreClientApplication
import it.valeriovaudi.vauthenticator.security.VAuthenticatorPasswordEncoder
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@EnableConfigurationProperties(SignUpConfirmationMailConfiguration::class)
@Configuration(proxyBeanMethods = false)
class UseCasesConfig {
    @Bean
    fun readClientApplication(clientApplicationRepository: ClientApplicationRepository) =
            ReadClientApplication(clientApplicationRepository)

    @Bean
    fun storeClientApplication(clientApplicationRepository: ClientApplicationRepository,
                               passwordEncoder: VAuthenticatorPasswordEncoder) =
            StoreClientApplication(clientApplicationRepository, passwordEncoder)

    @Bean
    fun signUpUseCase(clientAccountRepository: ClientApplicationRepository,
                      accountRepository: AccountRepository,
                      signUpConfirmationMailSender : SignUpConfirmationMailSender,
                      mailSenderService: MailSenderService,
                      vAuthenticatorPasswordEncoder : VAuthenticatorPasswordEncoder): SignUpUseCase {
        return SignUpUseCase(clientAccountRepository, accountRepository, signUpConfirmationMailSender, vAuthenticatorPasswordEncoder)
    }

    @Bean
    fun signUpConfirmationMailSender(mailSenderService: MailSenderService, signUpConfirmationMailConfiguration: SignUpConfirmationMailConfiguration) =
            SignUpConfirmationMailSender(mailSenderService, SimpleMailContextFactory(), signUpConfirmationMailConfiguration)
}