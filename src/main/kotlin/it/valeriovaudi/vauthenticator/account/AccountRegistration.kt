package it.valeriovaudi.vauthenticator.account

import org.springframework.amqp.core.*
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.amqp.dsl.Amqp
import org.springframework.integration.channel.DirectChannel
import org.springframework.integration.dsl.IntegrationFlows
import org.springframework.integration.dsl.MessageChannels
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service


interface AccountRegistration {

    fun execute(account: Account)

}

@Service
class MessagingAccountRegistration(private val accountRegistrationSender: SimpMessagingTemplate) : AccountRegistration {

    override fun execute(account: Account) {
        accountRegistrationSender.convertAndSend(account)
    }

}


@Configuration
class MessagingAccountRegistrationPipeline {

    @Bean
    fun accountRegistrationSender(accountRegistrationRequestChannel: DirectChannel) =
            SimpMessagingTemplate(accountRegistrationRequestChannel)

    @Bean
    fun accountRegistrationQueue(): Queue = Queue("account-registration", false, false, true)

    @Bean
    fun vauthenticatorRegistrationExchange(): Exchange =
            DirectExchange("vauthenticator-registration", false, false)

    @Bean
    fun storeMetricsBinder(accountRegistrationQueue: Queue,
                           vauthenticatorRegistrationExchange: Exchange) =
            Declarables(accountRegistrationQueue, vauthenticatorRegistrationExchange,
                    BindingBuilder
                            .bind(accountRegistrationQueue)
                            .to(vauthenticatorRegistrationExchange)
                            .with("account-registration")
                            .noargs()
            )

    @Bean
    fun accountRegistrationRequestChannel() = MessageChannels.direct()

    @Bean
    fun storeAccountProcess(
            rabbitTemplate: RabbitTemplate,
            accountRepository: MongoAccountRepository
    ) = IntegrationFlows.from(accountRegistrationRequestChannel())
            .handle { account: Account ->
                accountRepository.save(account)
                AccountConverter.fromDomainToRepresentation(account)
            }
            .handle(Amqp.outboundAdapter(rabbitTemplate)
                    .exchangeName("vauthenticator-registration")
                    .routingKey("account-registration"))
            .get()
}