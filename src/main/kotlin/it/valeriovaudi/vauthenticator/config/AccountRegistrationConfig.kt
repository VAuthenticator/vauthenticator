package it.valeriovaudi.vauthenticator.config

import com.fasterxml.jackson.databind.ObjectMapper
import it.valeriovaudi.vauthenticator.account.*
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
class AccountRegistrationConfig {

    @Bean
    fun bcryptAccountPasswordEncoder(passwordEncoder: PasswordEncoder) =
            BcryptAccountPasswordEncoder(passwordEncoder)

    @Bean
    fun accountRegistrationEventsListener(objectMapper: ObjectMapper) =
            AccountRegistrationEventsListener(objectMapper)

    @Bean
    fun rabbitMqAccountRegistrationEventPublisher(objectMapper: ObjectMapper,
                                                  rabbitTemplate: RabbitTemplate) =
            RabbitMqAccountRegistrationEventPublisher(objectMapper, rabbitTemplate)

    @Bean
    fun accountRegistration(accountRepository: AccountRepository,
                            passwordEncoder: AccountPasswordEncoder,
                            eventPublisher: AccountRegistrationEventPublisher) =
            AccountRegistration(accountRepository, passwordEncoder, eventPublisher)
}