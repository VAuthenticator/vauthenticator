package it.valeriovaudi.vauthenticator.account

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.amqp.rabbit.annotation.RabbitListener

class AccountSyncListener(
        private val objectMapper: ObjectMapper,
        private val accountRepository: AccountRepository
) {

    @RabbitListener(queues = ["account-sync"])
    fun accountStored(message: String) {
        println("account-stored")
        println(message)
        val readTree = objectMapper.readTree(message)
        val email = readTree.get("email").asText()
        val firstName = readTree.get("firstName").asText()
        val lastName = readTree.get("lastName").asText()
        println("email $email")
        accountRepository.accountFor(email)
                .ifPresent {account ->
                    account.firstName = firstName;
                    account.lastName = lastName;

                    accountRepository.update(account);
                }
        //send to mail sender
    }
}