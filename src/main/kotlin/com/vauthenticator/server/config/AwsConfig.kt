package com.vauthenticator.server.config

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider
import software.amazon.awssdk.auth.credentials.WebIdentityTokenFileCredentialsProvider
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.kms.KmsClient
import software.amazon.awssdk.services.s3.S3Client
import java.util.*

@Configuration(proxyBeanMethods = false)
class AwsConfig {

    @Bean("awsCredentialsProvider")
    @ConditionalOnProperty("iamUserAwsCredentialsProvider", havingValue = "true")
    fun iamUserAwsCredentialsProvider(): AwsCredentialsProvider {
        return EnvironmentVariableCredentialsProvider.create()
    }


    @Bean("awsCredentialsProvider")
    @ConditionalOnProperty("iamServiceAccountAwsCredentialsProvider", havingValue = "true")
    fun iamServiceAccountAwsCredentialsProvider(): AwsCredentialsProvider {
        return WebIdentityTokenFileCredentialsProvider.builder().roleSessionName("vauthenticator-${UUID.randomUUID()}").build()
    }

    @Bean
    fun s3Client(awsCredentialsProvider: AwsCredentialsProvider) = S3Client.builder()
            .credentialsProvider(awsCredentialsProvider)
            .build()

    @Bean
    fun kmsClient(awsCredentialsProvider: AwsCredentialsProvider) = KmsClient.builder()
            .credentialsProvider(awsCredentialsProvider)
            .build()

    @Bean
    fun dynamoDbClient(awsCredentialsProvider: AwsCredentialsProvider) = DynamoDbClient.builder()
            .credentialsProvider(awsCredentialsProvider)
            .build()
}