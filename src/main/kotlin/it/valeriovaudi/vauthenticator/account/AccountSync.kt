package it.valeriovaudi.vauthenticator.account

import com.fasterxml.jackson.databind.ObjectMapper
import it.valeriovaudi.aws.messaging.sqs.ReceiveMessageRequestFactory
import it.valeriovaudi.aws.messaging.sqs.SqsReactiveListener
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Flux
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import java.time.Duration

private val LOGGER: Logger = LoggerFactory.getLogger(AccountSyncListener::class.java)

@Configuration
class AccountSyncListenerConfig {

    @Bean
    fun accountSyncHandler(objectMapper: ObjectMapper,
                           accountRepository: AccountRepository): (String) -> Unit =
            AccountSyncHandler(objectMapper, accountRepository)

    @Bean
    fun accountSyncListener(@Value("\${vauthenticator.account-sync.listener.sleeping:10m}") sleeping: Duration,
                            receiveMessageRequestFactory: ReceiveMessageRequestFactory,
                            sqsAsyncClient: SqsAsyncClient,
                            accountSyncHandler: (String) -> Void) =
            AccountSyncListener(
                    SqsReactiveListener(
                            sleeping,
                            Flux.just(1).repeat(),
                            receiveMessageRequestFactory,
                            sqsAsyncClient,
                            accountSyncHandler
                    )
            )

    @Bean
    fun receiveMessageRequestFactory(@Value("\${vauthenticator.account-sync.listener.queueUrl}") queueUrl: String,
                                     @Value("\${vauthenticator.account-sync.listener.maxNumberOfMessages}") maxNumberOfMessages: Int,
                                     @Value("\${vauthenticator.account-sync.listener.visibilityTimeout}") visibilityTimeout: Int,
                                     @Value("\${vauthenticator.account-sync.listener.waitTimeSeconds}") waitTimeSeconds: Int
    ): ReceiveMessageRequestFactory {
        return ReceiveMessageRequestFactory(queueUrl, maxNumberOfMessages, visibilityTimeout, waitTimeSeconds)
    }


    @Bean
    fun awsCredentialsProvider(): AwsCredentialsProvider {
        return EnvironmentVariableCredentialsProvider.create()
    }


    @Bean
    fun sqsAsyncClient(@Value("\${aws.region}") awsRegion: String,
                       awsCredentialsProvider: AwsCredentialsProvider): SqsAsyncClient {
        return SqsAsyncClient.builder()
                .credentialsProvider(awsCredentialsProvider)
                .region(Region.of(awsRegion))
                .build()
    }
}

class AccountSyncListener(private val listener: SqsReactiveListener) : ApplicationRunner {

    override fun run(args: ApplicationArguments?) {
        listener.start()
    }
}

class AccountSyncHandler(private val objectMapper: ObjectMapper,
                         private val accountRepository: AccountRepository) : (String) -> Unit {
    override fun invoke(message: String) {
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