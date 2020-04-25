package it.valeriovaudi.vauthenticator.account

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.amqp.dsl.Amqp
import org.springframework.integration.annotation.Gateway
import org.springframework.integration.annotation.MessagingGateway
import org.springframework.integration.channel.DirectChannel
import org.springframework.integration.core.MessagingTemplate
import org.springframework.integration.dsl.IntegrationFlows.from
import org.springframework.integration.dsl.MessageChannels
import org.springframework.messaging.Message
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service


@Service
class MessagingAccountRegistration(private val passwordEncoder: PasswordEncoder,
                                   private val messagingTemplate: MessagingTemplate,
                                   private val accountRegistrationRequestChannel: DirectChannel,
                                   private val accountRegistrationGateway: AccountRegistrationGateway
) : AccountRegistration {

    override fun execute(account: Account) {
        val payload = account.copy(password = passwordEncoder.encode(account.password))

        /*val message = withPayload(payload)
                .setHeader("errorChannel", "accountRegistrationErrorChannel")
                .build()

        messagingTemplate.send(accountRegistrationRequestChannel, message)*/

        accountRegistrationGateway.execute(payload)
    }

}

@MessagingGateway(errorChannel = "accountRegistrationErrorChannel")
interface AccountRegistrationGateway {

    @Gateway(requestChannel = "accountRegistrationRequestChannel")
    fun execute(account: Account)

}

@Configuration
class MessagingAccountRegistrationPipeline {

    @Bean
    fun accountRegistrationRequestChannel() = MessageChannels.direct().get()

    @Bean
    fun accountRegistrationErrorChannel() = MessageChannels.publishSubscribe().get()

    @Autowired
    lateinit var objectMapper: ObjectMapper;

    @Autowired
    lateinit var rabbitTemplate: RabbitTemplate;

    @Autowired
    lateinit var accountRepository: MongoAccountRepository;

    @Bean
    fun storeAccountProcess() = from(accountRegistrationRequestChannel())
            .handle { account: Account -> storeAccount(account) }
            .handle(Amqp.outboundAdapter(rabbitTemplate)
                    .exchangeName("vauthenticator-registration")
                    .routingKey("account-registration"))
            .get()

    @Bean
    fun storeAccountErrorHandlingProcess() = from(accountRegistrationErrorChannel())
            .handle { errorMessage: Message<*> -> print(" $errorMessage") }
            .get()

    private fun storeAccount(account: Account) {
        accountRepository.create(account)
        AccountConverter.fromDomainToRepresentation(account)
                .let {
                    objectMapper.writeValueAsString(it)
                }
    }

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