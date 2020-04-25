package it.valeriovaudi.vauthenticator.config

import org.springframework.amqp.core.*
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.amqp.RabbitProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import org.springframework.integration.core.MessagingTemplate


@Configuration
class MessagingConfig {

    @Autowired
    lateinit var properties: RabbitProperties

    @Autowired
    lateinit var messageConverter: ObjectProvider<MessageConverter>

    @Bean
    fun accountRegistrationSender() = MessagingTemplate()

    @Bean
    fun accountRegistrationQueue(): Queue = Queue("account-registration", false, false, false)

    @Bean
    fun accountStoredQueue(): Queue = Queue("account-stored", false, false, false)

    @Bean
    fun accountOnAuthSystemCreationError(): Queue = Queue("account-on-auth-system-creation-error", false, false, false)

    @Bean
    fun vauthenticatorRegistrationExchange(): Exchange =
            DirectExchange("vauthenticator-registration", false, false)

    @Bean
    fun accountStoredQueueBinder(accountRegistrationQueue: Queue,
                                  vauthenticatorRegistrationExchange: Exchange) =
            Declarables(accountRegistrationQueue, vauthenticatorRegistrationExchange,
                    BindingBuilder
                            .bind(accountRegistrationQueue)
                            .to(vauthenticatorRegistrationExchange)
                            .with("account-registration")
                            .noargs())

    @Bean
    fun accountOnAuthSystemCreationErrorBinder(accountOnAuthSystemCreationError: Queue,
                                  vauthenticatorRegistrationExchange: Exchange) =
            Declarables(accountOnAuthSystemCreationError, vauthenticatorRegistrationExchange,
                    BindingBuilder
                            .bind(accountOnAuthSystemCreationError)
                            .to(vauthenticatorRegistrationExchange)
                            .with("account-on-auth-system-creation-error")
                            .noargs())

    @Bean
    fun accountStoredBinder(accountStoredQueue: Queue,
                            vauthenticatorRegistrationExchange: Exchange) =
            Declarables(accountStoredQueue, vauthenticatorRegistrationExchange,
                    BindingBuilder
                            .bind(accountStoredQueue)
                            .to(vauthenticatorRegistrationExchange)
                            .with("account-stored")
                            .noargs()
            )


    @Bean
    fun jackson2MessageConverter(): Jackson2JsonMessageConverter = Jackson2JsonMessageConverter()

    @Bean
    @Scope("prototype")
    fun rabbitTemplate(connectionFactory: ConnectionFactory): RabbitTemplate {
        val template = RabbitTemplate(connectionFactory)
        val messageConverter = this.messageConverter.getIfUnique()
        if (messageConverter != null) {
            template.messageConverter = messageConverter
        }
        return template
    }
}
