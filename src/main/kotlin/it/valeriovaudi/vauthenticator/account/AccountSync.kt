package it.valeriovaudi.vauthenticator.account

import com.fasterxml.jackson.databind.ObjectMapper
import it.valeriovaudi.aws.messaging.sqs.ReceiveMessageRequestFactory
import it.valeriovaudi.aws.messaging.sqs.SqsReactiveListener
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Flux
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import java.time.Duration
import java.util.function.Consumer

private val LOGGER: Logger = LoggerFactory.getLogger(AccountSyncHandler::class.java)

@Configuration
class AccountSyncListenerConfig {

    @Bean
    fun accountSyncHandler(objectMapper: ObjectMapper,
                           accountRepository: AccountRepository): Consumer<String> =
            AccountSyncHandler(objectMapper, accountRepository)

    @Bean
    fun accountSyncListener(@Value("\${vauthenticator.account-sync.listener.sleeping:10m}") sleeping: Duration,
                            receiveMessageRequestFactory: ReceiveMessageRequestFactory,
                            sqsAsyncClient: SqsAsyncClient,
                            accountSyncHandler: Consumer<String>): ApplicationRunner =
            SqsReactiveListener(
                    sleeping,
                    Flux.just(1).repeat(),
                    receiveMessageRequestFactory,
                    sqsAsyncClient,
                    accountSyncHandler
            ).let { listener ->
                ApplicationRunner { listener.start() }
            }
}

class AccountSyncHandler(private val objectMapper: ObjectMapper,
                         private val accountRepository: AccountRepository) : Consumer<String> {
    override fun accept(message: String) {
        LOGGER.debug("account-sync listener fired")
        LOGGER.debug(message)

        val readTree = objectMapper.readTree(message)
        val email = readTree.get("mail").asText()
        val firstName = readTree.get("firstName").asText()
        val lastName = readTree.get("lastName").asText()
        accountRepository.accountFor(email)
                .ifPresent { account ->
                    account.firstName = firstName;
                    account.lastName = lastName;

                    accountRepository.save(account);
                }
    }

}