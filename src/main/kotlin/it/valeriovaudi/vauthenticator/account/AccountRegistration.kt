package it.valeriovaudi.vauthenticator.account

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.amqp.dsl.Amqp
import org.springframework.integration.annotation.ServiceActivator
import org.springframework.integration.channel.DirectChannel
import org.springframework.integration.core.MessagingTemplate
import org.springframework.integration.dsl.EnricherSpec
import org.springframework.integration.dsl.IntegrationFlows.from
import org.springframework.integration.dsl.MessageChannels
import org.springframework.integration.dsl.Pollers
import org.springframework.messaging.Message
import org.springframework.messaging.MessageHandler
import org.springframework.messaging.MessageHeaders
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
    fun accountRegistrationErrorChannel() = MessageChannels.publishSubscribe()

    @Autowired
    lateinit var objectMapper: ObjectMapper;

    @Autowired
    lateinit var rabbitTemplate: RabbitTemplate;

    @Autowired
    lateinit var accountRepository: MongoAccountRepository;

    @Bean
    fun storeAccountProcess() = from(accountRegistrationRequestChannel())
            .enrich { t: EnricherSpec -> t.header(MessageHeaders.ERROR_CHANNEL, "accountRegistrationErrorChannel", true) }
            .log()
            .handle { account: Account -> storeAccount(account) }
            .handle(Amqp.outboundAdapter(rabbitTemplate)
                    .exchangeName("vauthenticator-registration")
                    .routingKey("account-registration"))
            .get()

    @Bean
    fun accountAlreadystoredPipeline() = from(accountRegistrationErrorChannel())
            .handle { e: Message<*> -> println("Errror: $e") }
            .get()

    private fun storeAccount(account: Account) {
        accountRepository.create(account)
        AccountConverter.fromDomainToRepresentation(account)
                .let {
                    objectMapper.writeValueAsString(it)
                }
    }

    @Bean
    fun poller() = Pollers.fixedRate(10);

    @Bean
    @ServiceActivator(inputChannel = "accountRegistrationErrorChannel")
    fun scatterGatherDistribution(): MessageHandler = MessageHandler { message -> println("Errror: $message") }
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