package it.valeriovaudi.vauthenticator.config

import it.valeriovaudi.aws.messaging.sqs.ReceiveMessageRequestFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.services.sqs.SqsAsyncClient


@Configuration(proxyBeanMethods = false)
class MessagingConfig {


    @Bean
    fun sqsAsyncClient(awsCredentialsProvider: AwsCredentialsProvider): SqsAsyncClient {
        return SqsAsyncClient.builder()
                .credentialsProvider(awsCredentialsProvider)
                .build()
    }

    @Bean
    fun receiveMessageRequestFactory(@Value("\${vauthenticator.account-sync.listener.queueUrl}") queueUrl: String,
                                     @Value("\${vauthenticator.account-sync.listener.maxNumberOfMessages}") maxNumberOfMessages: Int,
                                     @Value("\${vauthenticator.account-sync.listener.visibilityTimeout}") visibilityTimeout: Int,
                                     @Value("\${vauthenticator.account-sync.listener.waitTimeSeconds}") waitTimeSeconds: Int
    ): ReceiveMessageRequestFactory {
        return ReceiveMessageRequestFactory(queueUrl, maxNumberOfMessages, visibilityTimeout, waitTimeSeconds)
    }
}
