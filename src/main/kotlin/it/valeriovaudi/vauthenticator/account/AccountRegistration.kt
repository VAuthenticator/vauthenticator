package it.valeriovaudi.vauthenticator.account

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.amqp.dsl.Amqp
import org.springframework.integration.channel.DirectChannel
import org.springframework.integration.core.MessagingTemplate
import org.springframework.integration.dsl.IntegrationFlows
import org.springframework.integration.dsl.MessageChannels
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service


@Service
class MessagingAccountRegistration(private val passwordEncoder: PasswordEncoder,
                                   private val messagingTemplate: MessagingTemplate,
                                   private val accountRegistrationRequestChannel: DirectChannel) : AccountRegistration {

    override fun execute(account: Account) {
        messagingTemplate.convertAndSend(accountRegistrationRequestChannel, account.copy(password = passwordEncoder.encode(account.password)))
    }

}


@Configuration
class MessagingAccountRegistrationPipeline {

    @Bean
    fun accountRegistrationRequestChannel() = MessageChannels.direct()

    @Bean
    fun storeAccountProcess(
            objectMapper: ObjectMapper,
            rabbitTemplate: RabbitTemplate,
            accountRepository: MongoAccountRepository
    ) = IntegrationFlows.from(accountRegistrationRequestChannel())
            .handle { account: Account ->
                accountRepository.create(account)
                AccountConverter.fromDomainToRepresentation(account)
                        .let {
                            objectMapper.writeValueAsString(it)
                        }
            }
            .handle(Amqp.outboundAdapter(rabbitTemplate)
                    .exchangeName("vauthenticator-registration")
                    .routingKey("account-registration"))
            .get()
}

@Component
class AccountStoredListener(private val objectMapper: ObjectMapper) {

    @RabbitListener(queues = ["account-stored"])
    fun accountStored(message: String) {
        println(message)
        val readTree = objectMapper.readTree(message)
        val email = readTree.get("email")
        println("email $email")
        //send to mail sender
    }
}