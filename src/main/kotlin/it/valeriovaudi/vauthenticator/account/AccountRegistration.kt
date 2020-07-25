package it.valeriovaudi.vauthenticator.account

import com.fasterxml.jackson.databind.ObjectMapper
import it.valeriovaudi.vauthenticator.extentions.VAuthenticatorPasswordEncoder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate

class AccountRegistration(private val accountRepository: AccountRepository,
                          private val passwordEncoder: VAuthenticatorPasswordEncoder,
                          private val eventPublisher: AccountRegistrationEventPublisher) {

    fun execute(account: Account) {
        try {
            val password = passwordEncoder.encode(account.password)
            accountRepository.create(account.copy(password = password))
            eventPublisher.accountCreated(
                    AccountCreated(email = account.email,
                            firstName = account.firstName,
                            lastName = account.lastName)
            )
        } catch (e: AccountRegistrationException) {
            eventPublisher.accountCreationErrorOnAuthSystem(
                    AccountCreationErrorOnAuthSystem(email = account.email,
                            firstName = account.firstName,
                            lastName = account.lastName,
                            error = AccountRegistrationError(e.message!!))
            )
        }
    }
}

interface AccountRegistrationEventPublisher {
    fun accountCreated(accountCreated: AccountCreated)
    fun accountCreationErrorOnAuthSystem(accountCreationErrorOnAuthSystem: AccountCreationErrorOnAuthSystem)
}

class RabbitMqAccountRegistrationEventPublisher(
        private val objectMapper: ObjectMapper,
        private val rabbitTemplate: RabbitTemplate
) : AccountRegistrationEventPublisher {
    val exchange = "vauthenticator-registration"

    override fun accountCreated(accountCreated: AccountCreated) {
        rabbitTemplate.convertAndSend(
                exchange, "account-registration",
                objectMapper.writeValueAsString(accountCreated)
        )
    }

    override fun accountCreationErrorOnAuthSystem(accountCreationErrorOnAuthSystem: AccountCreationErrorOnAuthSystem) =
            rabbitTemplate.convertAndSend(
                    exchange, "account-on-auth-system-creation-error",
                    objectMapper.writeValueAsString(accountCreationErrorOnAuthSystem)
            )
}


class AccountRegistrationEventsListener(private val objectMapper: ObjectMapper) {

    private val LOGGER : Logger = LoggerFactory.getLogger(AccountRegistrationEventsListener::class.java)

    @RabbitListener(queues = ["account-on-auth-system-creation-error"])
    fun accountCreationErrorOnAuthSystem(message: String) {
        println("account-on-auth-system-creation-error")
        println(message)
        val readTree = objectMapper.readTree(message)
        val email = readTree.get("email")
        println("email $email")
        //send to mail sender
    }

    @RabbitListener(queues = ["account-stored"])
    fun accountStored(message: String) {

        LOGGER.debug("account-stored listener fired")
        LOGGER.debug(message)

        val readTree = objectMapper.readTree(message)
        val email = readTree.get("email")
        //send to mail sender
    }
}