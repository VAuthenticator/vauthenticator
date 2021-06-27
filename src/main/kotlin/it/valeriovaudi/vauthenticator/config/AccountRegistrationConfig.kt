package it.valeriovaudi.vauthenticator.config

import com.fasterxml.jackson.databind.ObjectMapper
import it.valeriovaudi.vauthenticator.account.AccountRepository
import it.valeriovaudi.vauthenticator.account.AccountSyncListener
import it.valeriovaudi.vauthenticator.extentions.BcryptVAuthenticatorPasswordEncoder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
class AccountRegistrationConfig {

    @Bean
    fun bcryptAccountPasswordEncoder(passwordEncoder: PasswordEncoder) =
            BcryptVAuthenticatorPasswordEncoder(passwordEncoder)

    @Bean
    fun accountSyncListener(objectMapper: ObjectMapper, accountRepository: AccountRepository) =
            AccountSyncListener(objectMapper, accountRepository)

}