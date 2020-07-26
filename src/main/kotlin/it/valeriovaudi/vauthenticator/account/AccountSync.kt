package it.valeriovaudi.vauthenticator.account

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener

private val LOGGER : Logger = LoggerFactory.getLogger(AccountSyncListener::class.java)

class AccountSyncListener(
        private val objectMapper: ObjectMapper,
        private val accountRepository: AccountRepository
) {

    @RabbitListener(queues = ["account-sync"])
    fun accountStored(message: String) {
        LOGGER.info("account-sync listener fired")
        LOGGER.info(message)

        val readTree = objectMapper.readTree(message)
        val email = readTree.get("email").asText()
        val firstName = readTree.get("firstName").asText()
        val lastName = readTree.get("lastName").asText()
        accountRepository.accountFor(email)
                .ifPresent {account ->
                    account.firstName = firstName;
                    account.lastName = lastName;

                    accountRepository.save(account);
                }
    }
}