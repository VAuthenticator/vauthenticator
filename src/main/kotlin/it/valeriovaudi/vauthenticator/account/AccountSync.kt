package it.valeriovaudi.vauthenticator.account

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val LOGGER : Logger = LoggerFactory.getLogger(AccountSyncListener::class.java)

class AccountSyncListener(
        private val objectMapper: ObjectMapper,
        private val accountRepository: AccountRepository
) {

    fun accountStored(message: String) {
        LOGGER.debug("account-sync listener fired")
        LOGGER.debug(message)

        val readTree = objectMapper.readTree(message)
        val email = readTree.get("mail").asText()
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